package edu.ub.sportshub.data.events.auth

import edu.ub.sportshub.data.enums.RegisterResult

class RegisterPerformedEvent(val registerResult: RegisterResult) : AuthEvent {
}