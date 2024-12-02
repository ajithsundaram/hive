package com.apis.hive.dto

data class DataDTO(
    var key: String? = null,
    var value: MutableMap<String,Any?>? = null,
    var ttl: Long? = null
)