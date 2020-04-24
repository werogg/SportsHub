package edu.ub.sportshub.data.models.event

import edu.ub.sportshub.data.data.DataAccessObject

abstract class EventDao : IEventDao, DataAccessObject() {
}