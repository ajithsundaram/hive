package com.apis.hive.dto

import com.fasterxml.jackson.annotation.JsonInclude

data class DataDTO(
    var key: String? = null,
    var value: MutableMap<String,Any?>? = null,
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var ttl: Long? = null
)