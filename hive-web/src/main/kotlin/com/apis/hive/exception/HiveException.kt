package com.apis.hive.exception
import com.apis.hive.util.AppConstant
import com.apis.hive.util.ErrorConstants
import com.apis.hive.util.ErrorInfo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

class TenantNotFoundException(msg: String) : Exception(msg)

class KeyNotFoundException(msg: String): Exception(msg)

class KeyAlreadyExistsException(msg: String): Exception(msg)

class StorageLimitExceededException(msg: String): Exception(msg)

class ServerErrorException(msg: String): Exception(msg)

@ControllerAdvice
class GlobalExceptionHandler {

    @ExceptionHandler(TenantNotFoundException::class)
    fun handleCommonException(ex: TenantNotFoundException): ResponseEntity<Any?> {
        val response = mutableMapOf<String, Any?>()
        response[AppConstant.MESSAGE] = ErrorConstants.INTERNAL_SERVER_ERROR.message
        response[AppConstant.ERROR_CODE] = ErrorConstants.INTERNAL_SERVER_ERROR.errorCode
        return ResponseEntity<Any?>(response, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(KeyNotFoundException::class)
    fun handleKeyNotFound(ex: KeyNotFoundException): ResponseEntity<Any?> {
        val response = mutableMapOf<String, Any?>()
        response[AppConstant.MESSAGE] = ErrorConstants.KEY_NOT_EXIST.message
        response[AppConstant.ERROR_CODE] = ErrorConstants.KEY_NOT_EXIST.errorCode
        return ResponseEntity<Any?>(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(KeyAlreadyExistsException::class)
    fun handleKeyAlreadyExist(ex: KeyAlreadyExistsException): ResponseEntity<Any?> {
        val response = mutableMapOf<String, Any?>()
        response[AppConstant.MESSAGE] = ErrorConstants.KEY_ALREADY_EXIST.message
        response[AppConstant.ERROR_CODE] = ErrorConstants.KEY_ALREADY_EXIST.errorCode
        return ResponseEntity<Any?>(response, HttpStatus.BAD_REQUEST)
    }

    @ExceptionHandler(StorageLimitExceededException::class)
    fun handleStorageLimitExceed(ex: StorageLimitExceededException): ResponseEntity<Any?> {
        val response = mutableMapOf<String, Any?>()
        response[AppConstant.MESSAGE] = ErrorConstants.DATA_LIMIT_EXCEEDED_FOR_TENANT.message
        response[AppConstant.ERROR_CODE] = ErrorConstants.DATA_LIMIT_EXCEEDED_FOR_TENANT.errorCode
        return ResponseEntity<Any?>(response, HttpStatus.BAD_REQUEST)
    }

}
