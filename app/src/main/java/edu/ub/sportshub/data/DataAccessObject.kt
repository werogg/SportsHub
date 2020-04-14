package edu.ub.sportshub.data

import edu.ub.sportshub.data.enums.DataType
import edu.ub.sportshub.data.events.DataEvent
import edu.ub.sportshub.data.listeners.*
import edu.ub.sportshub.models.Event
import edu.ub.sportshub.models.User

abstract class DataAccessObject : IDataAccessObject {
    var mListeners = mutableListOf<DataChangeListener>()

    override fun registerListener(dataChangeListener: DataChangeListener) {
        mListeners.add(dataChangeListener)
    }

    override fun executeListeners(event : DataEvent) {
        for (listener in mListeners) {
            listener.onDataLoaded(event)
        }
    }
}