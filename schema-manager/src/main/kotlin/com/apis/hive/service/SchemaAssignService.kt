package com.apis.hive.service

import com.apis.hive.repo.HiveDataRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class SchemaAssignService {
    @Autowired
    private lateinit var hiveDataRepository: HiveDataRepository

    @Value("\${app.tenant.storageSize}")
    private lateinit var storageSize: String

    @Transactional
    fun lockAndAssignSchema(tenantId: Long): String? {
        hiveDataRepository.lockForSchemaAssignment()
        val schemaName = hiveDataRepository.getOneFreeSchemaForTenant()
        if (schemaName != null) {
            hiveDataRepository.updateTenantForSchema(tenantId, schemaName)
            hiveDataRepository.saveInTenantDetails(tenantId, storageSize.toLong())
        }
        return schemaName
    }
}
