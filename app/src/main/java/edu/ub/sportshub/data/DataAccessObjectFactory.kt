package edu.ub.sportshub.data

import android.R
import android.content.res.Resources
import edu.ub.sportshub.data.enums.DatabaseType
import edu.ub.sportshub.data.models.event.EventDao
import edu.ub.sportshub.data.models.user.UserDao

object DataAccessObjectFactory : IDataAccessObjectFactory {
    var databaseType = DatabaseType.FIRESTORE

    override fun getUserDao() : UserDao {
        return Class.forName("edu.ub.sportshub.data.models.user.UserDao${databaseType.implementationName}").newInstance() as UserDao
    }

    override fun getEventDao(): EventDao {
        return Class.forName("edu.ub.sportshub.data.models.event.EventDao${databaseType.implementationName}").newInstance() as EventDao
    }
}