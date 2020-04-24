package edu.ub.sportshub.data.listeners

import edu.ub.sportshub.data.events.auth.AuthEvent

interface AuthPerformedListener {
    fun onActionPerformed(event: AuthEvent)
}