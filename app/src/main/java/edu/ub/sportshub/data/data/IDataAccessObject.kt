package edu.ub.sportshub.data.data

import edu.ub.sportshub.data.events.database.DataEvent
import edu.ub.sportshub.data.listeners.DataChangeListener
import edu.ub.sportshub.data.listeners.UserDataChangeListener
import edu.ub.sportshub.data.users.database.DataUser

interface IDataAccessObject {
    fun registerListener(dataChangeListener: DataChangeListener)
    fun registerListener(dataChangeListener: UserDataChangeListener)
    fun executeListeners(event : DataEvent)
    fun executeListeners(user : DataUser)
}