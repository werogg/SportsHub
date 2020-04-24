package edu.ub.sportshub.data.data

import edu.ub.sportshub.data.events.database.DataEvent
import edu.ub.sportshub.data.listeners.*

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