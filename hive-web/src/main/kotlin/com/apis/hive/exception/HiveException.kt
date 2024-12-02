package com.apis.hive.exception
import com.apis.hive.util.AppConstant
import com.apis.hive.util.ErrorConstants
import com.apis.hive.util.ErrorInfo
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

class HiveException(var errorInfo: ErrorInfo) : Exception(errorInfo.message)

@ControllerAdvice
class GlobalExceptionHandler {
    @ExceptionHandler(HiveException::class)
    fun handleHiveException(ex: HiveException): ResponseEntity<Any?> {
        val response = mutableMapOf<String, Any?>()
        response[AppConstant.MESSAGE] = ex.errorInfo.message
        response[AppConstant.ERROR_CODE] = ex.errorInfo.errorCode
        return ResponseEntity<Any?>(response, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    @ExceptionHandler(Exception::class)
    fun handleCommonException(ex: Exception): ResponseEntity<Any?> {
        val response = mutableMapOf<String, Any?>()
        response[AppConstant.MESSAGE] = ErrorConstants.INTERNAL_SERVER_ERROR.message
        response[AppConstant.ERROR_CODE] = ErrorConstants.INTERNAL_SERVER_ERROR.errorCode
        return ResponseEntity<Any?>(response, HttpStatus.INTERNAL_SERVER_ERROR)
    }
}
