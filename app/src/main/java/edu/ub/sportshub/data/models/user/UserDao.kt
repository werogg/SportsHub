package edu.ub.sportshub.data.models.user

import edu.ub.sportshub.data.DataAccessObject

abstract class UserDao : IUserDao, DataAccessObject() {
}