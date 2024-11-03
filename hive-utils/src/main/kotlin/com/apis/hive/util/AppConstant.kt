package com.apis.hive.util

object AppConstant {

    const val DATA_SOURCE_PROPERTY_PREFIX = "app.datasource.hive"

    const val HIVE_DATA_SOURCE = "hiveDataSource"
    const val TENANT_DB_PREFIX = "data"
    const val HIVE_SCHEMA_NAME = "hive"
    const val DB_ASSIGNMENT_LOCK = "DB_ASSIGNMENT_LOCK"
    const val TENANT_ID_HEADER = "tenantId"

    const val EMAIL = "email"
    const val TENANT_ID = "tenantId"


    // Tables and Columns

    const val SCHEMA_DETAILS_NAME = "name"
    const val SCHEME_DETAILS_TENANT_ID = "tenantId"

    const val TENANT_DETAILS_TENANT_ID = "tenantId"
    const val TENANT_DETAILS_STORAGE_SIZE = "storageSize"

    const val DATA_TABLE = "Data"
    const val SCHEMA_DETAILS_TABLE = "SchemaDetails"
    const val TENANT_DETAILS_TABLE = "TenantDetails"


    const val DATA_ID = "id"
    const val DATA_KEY = "key"
    const val DATA_VALUE = "value"
    const val DATA_TTL = "ttl"

//    api response params
    const val TOKEN = "token"
    const val MESSAGE = "message"
    const val ERROR_CODE = "errorCode"

    // query condition
    const val TENANT_ID_CONDITION = "$TENANT_ID = ?"
    const val KEY_CONDITION = "`$DATA_KEY` = ?"

    // endpoints
    const val ASSOCIATE_SCHEMA_ENDPOINT = "/db/associateSchema"
    const val CREATE_TENANT_ENDPOINT = "/tenant/create"
    const val LOGIN_TENANT_ENDPOINT = "/tenant/login"

    val ISC_ENDPOINTS = setOf(ASSOCIATE_SCHEMA_ENDPOINT)
    val PUBLIC_ENDPOINTS = setOf(CREATE_TENANT_ENDPOINT, LOGIN_TENANT_ENDPOINT)
}

object APIConstant {

    // api params

    const val KEY = "key"
    const val DATA = "value"
    const val TTL_IN_SECONDS = "ttl"
}
