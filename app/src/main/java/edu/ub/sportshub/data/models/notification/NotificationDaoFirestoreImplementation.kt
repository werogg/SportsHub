package edu.ub.sportshub.data.models.notification

import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FieldValue
import edu.ub.sportshub.data.enums.NotificationType
import edu.ub.sportshub.data.events.database.UserNotificationsLoadedEvent
import edu.ub.sportshub.helpers.StoreDatabaseHelper
import edu.ub.sportshub.models.Notification
import edu.ub.sportshub.models.NotificationFollowed
import edu.ub.sportshub.models.User
import edu.ub.sportshub.utils.StringUtils

class NotificationDaoFirestoreImplementation : NotificationDao() {

    override fun fetchUserNotifications(userId: String) {
        val notifications = mutableListOf<Pair<Notification, User>>()
        val storeDatabaseHelper = StoreDatabaseHelper()
        storeDatabaseHelper.retrieveUser(userId).addOnSuccessListener {
            val user = it.toObject(User::class.java)

            for (notificationId in user!!.getNotifications()) {
                storeDatabaseHelper.retrieveNotification(notificationId).addOnSuccessListener {not ->
                    val notification = not.toObject(Notification::class.java)

                    storeDatabaseHelper.retrieveUser(notification!!.getCreatorUid())
                        .addOnSuccessListener { creator ->
                            val creatorUser = creator.toObject(User::class.java)
                            notifications.add(Pair(notification, creatorUser!!))

                            executeListeners(UserNotificationsLoadedEvent(notifications))
                    }
                }
            }
        }
    }

    override fun sendNotification(creatorId : String, recieverId : String, notificationType: NotificationType) {
        val storeDatabaseHelper = StoreDatabaseHelper()

        if (notificationType == NotificationType.FOLLOWED) {
            val notificationId = StringUtils.hashString(creatorId, "MD5")
            val notification = NotificationFollowed(notificationId, recieverId, Timestamp.now(), creatorId)
            storeDatabaseHelper.getNotificationsCollection().document(notificationId).set(notification).addOnSuccessListener {
                storeDatabaseHelper.getUsersCollection().document(recieverId).update("notifications", FieldValue.arrayUnion(notificationId)).addOnFailureListener {
                    // ERROR
                }
            }


        } else if (notificationType == NotificationType.ASSIST) {

        }
    }

}