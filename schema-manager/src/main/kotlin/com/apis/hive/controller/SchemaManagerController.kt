package com.apis.hive.controller

import com.apis.hive.dto.AssociateSchemaRequest
import com.apis.hive.dto.AssociateSchemaResponse
import com.apis.hive.dto.AssociateSchemaStatus
import com.apis.hive.dto.SchemaDetails
import com.apis.hive.service.SchemaManagerService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Component
@RestController
@RequestMapping("/db")
class SchemaManagerController {
    @Autowired
    private lateinit var schemaManagerService: SchemaManagerService

    private var logger = LoggerFactory.getLogger(this::class.java)

    @PostMapping("/associateSchema")
    fun associateSchemaToTenant(@RequestBody associateSchemaRequest: AssociateSchemaRequest): ResponseEntity<AssociateSchemaResponse> {
        val tenantId = associateSchemaRequest.tenantId
        if (tenantId == null || tenantId <= 0L) {
            return ResponseEntity(
                AssociateSchemaResponse(AssociateSchemaStatus.INVALID_INPUT),
                null,
                HttpStatus.BAD_REQUEST
            )
        }
        logger.info("Getting Schema for tenant {}", tenantId)
        val assignedSchema = schemaManagerService.getFreeSchema(tenantId)
        val (associateSchemaResponse, status) = if (assignedSchema != null) {
            logger.error("Schema {} Associated with tenant {}", assignedSchema, tenantId)
            Pair(AssociateSchemaResponse(AssociateSchemaStatus.SUCCESS, SchemaDetails(assignedSchema, tenantId)), HttpStatus.OK)
        } else {
            logger.error("No Schema found for this tenant {}", tenantId)
            Pair(
                AssociateSchemaResponse(
                    AssociateSchemaStatus.FAILURE,
                SchemaDetails(null, tenantId)
            ), HttpStatus.INTERNAL_SERVER_ERROR)
        }
        return ResponseEntity(associateSchemaResponse, null, status)
    }
}
