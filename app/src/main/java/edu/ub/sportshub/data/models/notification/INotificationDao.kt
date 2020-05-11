package edu.ub.sportshub.data.models.notification

import edu.ub.sportshub.data.enums.NotificationType

interface INotificationDao {
    fun fetchUserNotifications(userId : String)
    fun sendNotification(creatorId : String, recieverId : String, notificationType: NotificationType)
}