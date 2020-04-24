package edu.ub.sportshub.data.auth

import edu.ub.sportshub.data.events.auth.AuthEvent
import edu.ub.sportshub.data.listeners.AuthPerformedListener

abstract class AuthHandler : IAuthHandler {
    var mListeners = mutableListOf<AuthPerformedListener>()

    override fun registerListener(authPerformedListener: AuthPerformedListener) {
        mListeners.add(authPerformedListener)
    }

    override fun executeListeners(event: AuthEvent) {
        for (listener in mListeners) {
            listener.onActionPerformed(event)
        }
    }
}