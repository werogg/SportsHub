package edu.ub.sportshub.data.data

import edu.ub.sportshub.data.enums.DatabaseType
import edu.ub.sportshub.data.models.event.EventDao
import edu.ub.sportshub.data.models.notification.NotificationDao
import edu.ub.sportshub.data.models.user.UserDao

object DataAccessObjectFactory :
    IDataAccessObjectFactory {
    var databaseType = DatabaseType.FIRESTORE

    override fun getUserDao() : UserDao {
        return Class.forName("edu.ub.sportshub.data.models.user.UserDao${databaseType.implementationName}Implementation").newInstance() as UserDao
    }

    override fun getEventDao(): EventDao {
        return Class.forName("edu.ub.sportshub.data.models.event.EventDao${databaseType.implementationName}Implementation").newInstance() as EventDao
    }

    override fun getNotificationDao(): NotificationDao {
        return Class.forName("edu.ub.sportshub.data.models.notification.NotificationDao${databaseType.implementationName}Implementation").newInstance() as NotificationDao
    }
}