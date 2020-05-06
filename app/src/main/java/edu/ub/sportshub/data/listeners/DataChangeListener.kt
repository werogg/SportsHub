package edu.ub.sportshub.data.listeners

import edu.ub.sportshub.data.events.database.DataEvent
import edu.ub.sportshub.data.users.database.DataUser

interface DataChangeListener {
    fun onDataLoaded(event : DataEvent)
}