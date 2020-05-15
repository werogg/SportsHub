package edu.ub.sportshub.data.data

import edu.ub.sportshub.data.models.event.IEventDao
import edu.ub.sportshub.data.models.notification.INotificationDao
import edu.ub.sportshub.data.models.user.IUserDao

interface IDataAccessObjectFactory {
    fun getUserDao() : IUserDao
    fun getEventDao() : IEventDao
    fun getNotificationDao() : INotificationDao
}