package edu.ub.sportshub.data

import edu.ub.sportshub.data.models.event.EventDao
import edu.ub.sportshub.data.models.user.UserDao

interface IDataAccessObjectFactory {
    fun getUserDao() : UserDao
    fun getEventDao() : EventDao
}