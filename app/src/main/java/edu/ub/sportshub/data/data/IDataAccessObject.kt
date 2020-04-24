package edu.ub.sportshub.data.data

import edu.ub.sportshub.data.events.database.DataEvent
import edu.ub.sportshub.data.listeners.DataChangeListener

interface IDataAccessObject {
    fun registerListener(dataChangeListener: DataChangeListener)
    fun executeListeners(event : DataEvent)
}