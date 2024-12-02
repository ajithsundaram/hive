package com.apis.hive.dto

data class DataDTO(
    val key: String? = null,
    val value: MutableMap<String,Any?>? = null,
    var ttl: Long? = null
)