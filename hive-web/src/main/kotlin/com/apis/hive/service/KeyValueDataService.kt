package com.apis.hive.service

import com.apis.hive.dto.DataDTO
import com.apis.hive.entity.Data
import com.apis.hive.entity.TenantKey
import com.apis.hive.exception.KeyAlreadyExistsException
import com.apis.hive.exception.KeyNotFoundException
import com.apis.hive.exception.StorageLimitExceededException
import com.apis.hive.repository.DataRepo
import com.apis.hive.repository.TenantDetailsRepo
import com.apis.hive.util.AppConstant
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.util.regex.Pattern

@Service
class KeyValueDataService {
    private var objectMapper =  ObjectMapper()
    @Autowired
    private lateinit var dataRepo: DataRepo
    @Autowired
    private lateinit var tenantDetailsRepo : TenantDetailsRepo
    @Autowired
    private lateinit var tenantService: TenantService
    private var logger = LoggerFactory.getLogger(this::class.java)

    fun findDataByKey(key: String): Data? {
        val tenantId = tenantService.getTenantId()
        return dataRepo.findByTenantIdAndKey(tenantId, key) ?: throw KeyNotFoundException("$key not found")
    }

    fun bulkSaveData(inputData: List<Map<String, Any?>>) {
        val tenantId = tenantService.getTenantId()
        validateTenantLimit(inputData.size)
        try {
            val data = objectMapper.convertValue(inputData, object : TypeReference<List<DataDTO>>() {}).map { dataDto ->
                dataDto.ttl = if( dataDto.ttl != null) System.currentTimeMillis() + (dataDto.ttl!! * 1000) else null
                Data().apply {
                    id = TenantKey(tenantId, dataDto.key)
                    value = dataDto.value
                    ttl = dataDto.ttl
                }
            }
            dataRepo.saveAll(data)
            dataRepo.flush()
        } catch (ex: Exception) {
            logger.error("exception while saving data",ex)
            if(Pattern.compile(AppConstant.DUPLICATE_KEY_REGEX).matcher(ex.cause?.cause?.message ?: "").find()) {
                throw KeyAlreadyExistsException("duplicate key found")
            } else throw ex
        }
    }

    fun deleteDataByKey(key: String) {
        val result = findDataByKey(key)
        if (result != null) {
            val tenantId = tenantService.getTenantId()
            dataRepo.deleteById(TenantKey(tenantId,key))
        } else {
            throw KeyNotFoundException(key)
        }
    }

    fun validateTenantLimit(currentAdditionCount:Int) {
        try {
            val tenantId = tenantService.getTenantId()
            val tenantLimit = tenantDetailsRepo.findById(tenantId).get().storageSize!!
            val existingCount = dataRepo.getValidKeyCount(tenantId)
            if ( tenantLimit < existingCount+currentAdditionCount ) {
                throw StorageLimitExceededException("existing count $existingCount current addition $currentAdditionCount")
            }
        } catch (ex: Exception){
            logger.error("Exception while checking tenant data limit",ex)
            throw ex
        }
    }
}
