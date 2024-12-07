package com.apis.hive.dto

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

class DataDTO {
    @JsonProperty("key")
    var dataKey: String? = null
    @JsonProperty("value")
    var dataValue: MutableMap<String,Any?>? = null
    @JsonInclude(JsonInclude.Include.NON_NULL)
    var ttl: Long? = null
}