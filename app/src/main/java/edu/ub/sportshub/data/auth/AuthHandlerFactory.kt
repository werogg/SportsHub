package edu.ub.sportshub.data.auth

import edu.ub.sportshub.data.enums.AuthType

object AuthHandlerFactory : IAuthHandlerFactory {
    var authType = AuthType.FIREAUTH

    override fun getLoginHandler(): LoginHandler {
        return Class.forName("edu.ub.sportshub.data.auth.${authType.implementationName}LoginHandler").newInstance() as LoginHandler
    }

    override fun getRegisterHandler(): RegisterHandler {
        return Class.forName("edu.ub.sportshub.data.auth.${authType.implementationName}RegisterHandler").newInstance() as RegisterHandler
    }
}