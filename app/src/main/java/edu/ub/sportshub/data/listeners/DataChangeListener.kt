package edu.ub.sportshub.data.listeners

import edu.ub.sportshub.data.events.DataEvent

interface DataChangeListener {
    fun onDataLoaded(event : DataEvent)
}