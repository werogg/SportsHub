package edu.ub.sportshub.data.data

import edu.ub.sportshub.data.events.database.DataEvent
import edu.ub.sportshub.data.listeners.*
import edu.ub.sportshub.data.users.database.DataUser

abstract class DataAccessObject : IDataAccessObject {
    var mListeners = mutableListOf<DataChangeListener>()
    var userListeners = mutableListOf<UserDataChangeListener>()

    override fun registerListener(dataChangeListener: DataChangeListener) {
        mListeners.add(dataChangeListener)
    }

    override fun registerListener(userDataChangeListener: UserDataChangeListener) {
        userListeners.add(userDataChangeListener)
    }

    override fun executeListeners(event : DataEvent) {
        for (listener in mListeners) {
            listener.onDataLoaded(event)
        }
    }

    override fun executeListeners(user : DataUser) {
        for (listener in userListeners) {
            listener.onDataLoaded(user)
        }
    }

}