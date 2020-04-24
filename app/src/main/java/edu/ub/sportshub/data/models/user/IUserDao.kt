package edu.ub.sportshub.data.models.user

import edu.ub.sportshub.data.data.IDataAccessObject

interface IUserDao : IDataAccessObject {
    fun fetchUser(uid : String)
}