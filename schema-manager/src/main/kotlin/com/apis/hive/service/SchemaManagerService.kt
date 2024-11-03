package com.apis.hive.service

import com.apis.hive.handler.SchemaMigrationHandler
import com.apis.hive.repo.HiveDataRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SchemaManagerService {
    @Autowired
    private lateinit var hiveDataRepository: HiveDataRepository
    @Autowired
    private lateinit var schemaMigrationHandler: SchemaMigrationHandler
    @Autowired
    private lateinit var schemaAssignService: SchemaAssignService

    private val logger = LoggerFactory.getLogger(this::class.java)

    fun getFreeSchema(tenantId: Long): String? {
        var schemaName: String? = null
        try {
            val freeSchemaCount = hiveDataRepository.getUnassignedSchemaCount()
            if (freeSchemaCount == 0) {
                logger.info("No Free Schema to Associate for tenant {}", tenantId)
            } else {
                schemaName = schemaAssignService.lockAndAssignSchema(tenantId)
            }
        } catch (ex: Exception) {
            println("exception while assigning schema for tenant $tenantId" + ex.message)
            logger.error("exception while assigning schema for tenant {} -> {}", tenantId, ex)
        } finally {
            schemaMigrationHandler.checkAndInitiateFreeSchemaCreation()
        }
        return schemaName
    }
}
