package edu.ub.sportshub.data.auth

import edu.ub.sportshub.data.events.auth.AuthEvent
import edu.ub.sportshub.data.listeners.AuthPerformedListener

interface IAuthHandler {
    fun registerListener(authPerformedListener: AuthPerformedListener)
    fun executeListeners(event : AuthEvent)
}