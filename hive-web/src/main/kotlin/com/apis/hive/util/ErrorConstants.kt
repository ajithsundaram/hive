package com.apis.hive.util

object ErrorConstants {
    val KEY_NOT_EXIST = ErrorInfo("Key not found", "1000")
    val KEY_ALREADY_EXIST = ErrorInfo("Key already exist", "2000")
    val INVALID_INPUT = ErrorInfo("Invalid input", "4000")
    val INVALID_TENANT = ErrorInfo("Tenant not found", "3000")
    val INTERNAL_SERVER_ERROR = ErrorInfo("Internal server error ", "5000")
    val DATA_LIMIT_EXCEEDED_FOR_TENANT = ErrorInfo("Data limit exceeded","6000")
}

data class ErrorInfo(val message: String, val errorCode: String)
