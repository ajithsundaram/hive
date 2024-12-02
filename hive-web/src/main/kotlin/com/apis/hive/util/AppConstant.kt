package com.apis.hive.util

object AppConstant {
    const val TENANT_ID = "tenantId"
    const val HIVE_SCHEMA_NAME = "hive"
    //api params
    const val TOKEN = "token"
    const val MESSAGE = "message"
    const val ERROR_CODE = "errorCode"
    const val STORAGE_SIZE = "storageSize"
    //endpoints
    const val CREATE_TENANT_ENDPOINT = "/tenant/create"
    const val LOGIN_TENANT_ENDPOINT = "/tenant/login"
    val PUBLIC_ENDPOINTS = setOf(CREATE_TENANT_ENDPOINT, LOGIN_TENANT_ENDPOINT)
    val DUPLICATE_KEY_REGEX = "Duplicate entry '.*?-(.*?)' for key 'data.PRIMARY'"
}

