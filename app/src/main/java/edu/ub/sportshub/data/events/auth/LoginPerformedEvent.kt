package edu.ub.sportshub.data.events.auth

import edu.ub.sportshub.data.enums.LoginResult

class LoginPerformedEvent(val loginResult: LoginResult) : AuthEvent {
}