package edu.ub.sportshub.data.auth

interface IRegisterHandler {
    fun performRegister(username: String, fullname: String, email: String, pass: String, defaultImage: String)
}