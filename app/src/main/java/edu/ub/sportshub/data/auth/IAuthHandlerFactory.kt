package edu.ub.sportshub.data.auth

interface IAuthHandlerFactory {
    fun getLoginHandler() : ILoginHandler
    fun getRegisterHandler() : IRegisterHandler
}