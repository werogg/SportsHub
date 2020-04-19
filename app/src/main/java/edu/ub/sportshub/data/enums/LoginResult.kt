package edu.ub.sportshub.data.enums

enum class LoginResult {
    USER_BANNED,
    WRONG_PASSWORD,
    TOO_MUCH_REQUESTS,
    USER_NOT_VERIFIED,
    UNKNOWN_EXCEPTION,
    WRONG_USERNAME,
    MISSING_FIELDS,
    SUCCESS
}