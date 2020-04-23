package edu.ub.sportshub.data.auth

interface ILoginHandler {
    fun performLogin(username: String, password: String)
}