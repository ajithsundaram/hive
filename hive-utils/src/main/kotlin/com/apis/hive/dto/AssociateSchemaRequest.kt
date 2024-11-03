package com.apis.hive.dto

class AssociateSchemaRequest {
    val tenantId: Long? = null
}

class AssociateSchemaResponse(val status: AssociateSchemaStatus, val schemaDetails: SchemaDetails? = null)

enum class AssociateSchemaStatus {
    SUCCESS,
    FAILURE,
    INVALID_INPUT
}

data class SchemaDetails(
    var name: String? = null,
    var tenant: Long? = null
)

data class TenantDetails(
    val tenantId : String? = null,
    val storageSize: Long? = null
)