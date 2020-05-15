package edu.ub.sportshub.data.models.notification

import edu.ub.sportshub.data.enums.NotificationType
import edu.ub.sportshub.models.Notification

interface INotificationDao {
    fun fetchUserNotifications(userId : String)
    fun sendNotification(creatorId : String, recieverId : String, notificationType: NotificationType)
    fun sendEventNotificationToFollowers(userId: String, eventName: String, notificationType: NotificationType)
    fun sendEventNotificationToCreator(creatorId: String, recieverId: String, eventName: String, notificationType: NotificationType)
    fun markNotificationsAsChecked(notificationList : MutableList<Notification>)
}