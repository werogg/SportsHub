package edu.ub.sportshub.data

import edu.ub.sportshub.data.enums.DataType
import edu.ub.sportshub.data.events.DataEvent
import edu.ub.sportshub.data.listeners.DataChangeListener

interface IDataAccessObject {
    fun registerListener(dataChangeListener: DataChangeListener)
    fun executeListeners(event : DataEvent)
}