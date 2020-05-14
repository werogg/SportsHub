package edu.ub.sportshub.data.models.notification

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import edu.ub.sportshub.data.enums.NotificationType
import edu.ub.sportshub.data.events.database.UserNotificationsLoadedEvent
import edu.ub.sportshub.helpers.StoreDatabaseHelper
import edu.ub.sportshub.models.Notification
import edu.ub.sportshub.models.NotificationAssist
import edu.ub.sportshub.models.NotificationFollowed
import edu.ub.sportshub.models.User
import edu.ub.sportshub.utils.StringUtils
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
                        "ASSIST" -> notification = not.toObject(NotificationAssist::class.java)
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

        if (notificationType == NotificationType.FOLLOWED) {
            val notificationId = StringUtils.hashString(creatorId, "MD5")
            val notification = NotificationFollowed(notificationId, recieverId, Timestamp.now(), creatorId, false, NotificationType.FOLLOWED)
            storeDatabaseHelper.getNotificationsCollection().document(notificationId).set(notification).addOnSuccessListener {
                storeDatabaseHelper.getUsersCollection().document(recieverId).update("notifications", FieldValue.arrayUnion(notificationId)).addOnSuccessListener {
                    storeDatabaseHelper.getUsersCollection().document(recieverId).collection("notifications").document(notificationId).set(
                        notification
                    )
                }
            }


        } else if (notificationType == NotificationType.ASSIST) {
            val notificationId = StringUtils.hashString(creatorId, "MD5")
            val notification = NotificationAssist(notificationId, recieverId, Timestamp.now(), creatorId, false, NotificationType.ASSIST)
            storeDatabaseHelper.getNotificationsCollection().document(notificationId).set(notification).addOnSuccessListener {
                storeDatabaseHelper.getUsersCollection().document(recieverId).update("notifications", FieldValue.arrayUnion(notificationId)).addOnFailureListener {
                    // ERROR
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