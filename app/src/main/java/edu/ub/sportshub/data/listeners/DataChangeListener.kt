package edu.ub.sportshub.data.listeners

import edu.ub.sportshub.data.events.database.DataEvent

interface DataChangeListener {
    fun onDataLoaded(event : DataEvent)
}