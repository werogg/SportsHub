package edu.ub.sportshub.data.models.notification

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import edu.ub.sportshub.data.enums.NotificationType
import edu.ub.sportshub.data.events.database.UserNotificationsLoadedEvent
import edu.ub.sportshub.helpers.StoreDatabaseHelper
import edu.ub.sportshub.models.*
import edu.ub.sportshub.utils.StringUtils
import java.lang.Exception
import java.util.*

class NotificationDaoFirestoreImplementation : NotificationDao() {

    override fun fetchUserNotifications(userId: String) {
        val notifications = mutableListOf<Pair<Notification, User>>()
        val storeDatabaseHelper = StoreDatabaseHelper()
        storeDatabaseHelper.retrieveUser(userId).addOnSuccessListener {
            val user = it.toObject(User::class.java)

            for (notificationId in user!!.getNotifications()) {
                storeDatabaseHelper.retrieveNotification(notificationId).addOnSuccessListener {not ->
                    var notification : Notification? = null

                    when (not.get("notificationType")) {
                        "FOLLOWED" -> notification = not.toObject(NotificationFollowed::class.java)
                        "ASSIST_TO_CREATOR" -> notification = not.toObject(NotificationAssist::class.java)
                        "ASSIST_TO_FOLLOWERS" -> notification = not.toObject(NotificationAssist::class.java)
                        "LIKED" -> notification = not.toObject(NotificationLiked::class.java)
                    }

                    if (notification != null) {
                        storeDatabaseHelper.retrieveUser(notification.getCreatorUid())
                            .addOnSuccessListener { creator ->
                                val creatorUser = creator.toObject(User::class.java)
                                val today = Timestamp.now().toDate()
                                val lastMonthDate = Date(today.year, today.month - 1, today.day)

                                if (notification.getDate().toDate().after(lastMonthDate)) {
                                    notifications.add(Pair(notification, creatorUser!!))
                                    executeListeners(UserNotificationsLoadedEvent(notifications))
                                }
                            }
                    }
                }
            }
        }
    }

    override fun sendNotification(creatorId : String, recieverId : String, notificationType: NotificationType) {
        val storeDatabaseHelper = StoreDatabaseHelper()

        when (notificationType) {
            NotificationType.FOLLOWED -> {
                val notificationId = StringUtils.hashString(creatorId, "MD5")
                val notification = NotificationFollowed(notificationId, recieverId, Timestamp.now(), creatorId, false, notificationType)
                storeDatabaseHelper.getNotificationsCollection().document(notificationId).set(notification).addOnSuccessListener {
                    storeDatabaseHelper.getUsersCollection().document(recieverId).update("notifications", FieldValue.arrayUnion(notificationId)).addOnSuccessListener {
                        storeDatabaseHelper.getUsersCollection().document(recieverId).collection("notifications").document(notificationId).set(
                            notification
                        )
                    }
                }
            }
        }
    }

    override fun sendEventNotificationToFollowers(userId: String, eventName: String, notificationType: NotificationType) {
        val storeDatabaseHelper = StoreDatabaseHelper()

        storeDatabaseHelper.retrieveUser(userId).addOnSuccessListener {
            val user = it.toObject(User::class.java)
            for (followerId in user!!.getFollowersUsers()) {
                val notificationId = StringUtils.generateRandomId()
                val notification = NotificationAssist(notificationId, followerId, Timestamp.now(), userId, false, NotificationType.ASSIST_TO_FOLLOWERS, eventName)

                storeDatabaseHelper.getNotificationsCollection().document(notificationId).set(notification).addOnSuccessListener {
                    storeDatabaseHelper.getUsersCollection().document(followerId).update("notifications", FieldValue.arrayUnion(notificationId)).addOnSuccessListener {
                        storeDatabaseHelper.getUsersCollection().document(followerId).collection("notifications").document(notificationId).set(
                            notification
                        )
                    }
                }
            }
        }
    }

    override fun sendEventNotificationToCreator(creatorId: String, recieverId: String, eventName: String, notificationType: NotificationType) {
        val storeDatabaseHelper = StoreDatabaseHelper()
        val notificationId = StringUtils.generateRandomId()

        val notification = when (notificationType) {
            NotificationType.ASSIST_TO_CREATOR -> {
                NotificationAssist(notificationId, recieverId, Timestamp.now(), creatorId, false, notificationType, eventName)
            }

            NotificationType.LIKED -> {
                NotificationLiked(notificationId, recieverId, Timestamp.now(), creatorId, false, notificationType, eventName)
            }
            else -> null
        }

        if (notification != null) {
            storeDatabaseHelper.getNotificationsCollection().document(notificationId).set(notification).addOnSuccessListener {
                storeDatabaseHelper.getUsersCollection().document(recieverId).update("notifications", FieldValue.arrayUnion(notificationId)).addOnSuccessListener {
                    storeDatabaseHelper.getUsersCollection().document(recieverId).collection("notifications").document(notificationId).set(
                        notification
                    )
                }
            }
        }
    }

    override fun markNotificationsAsChecked(notificationList : MutableList<Notification>) {
        val storeDatabaseHelper = StoreDatabaseHelper()

        for (notification in notificationList) {
            storeDatabaseHelper.getNotificationsCollection().document(notification.getId()).update(
                "checked", true
            ).addOnFailureListener {
                // ERROR
            }
        }
    }

}