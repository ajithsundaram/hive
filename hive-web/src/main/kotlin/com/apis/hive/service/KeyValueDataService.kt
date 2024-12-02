package com.apis.hive.service

import com.apis.hive.configuration.TenantAuthenticationToken
import com.apis.hive.dto.DataDTO
import com.apis.hive.entity.Data
import com.apis.hive.entity.TenantKey
import com.apis.hive.exception.HiveException
import com.apis.hive.repository.DataRepo
import com.apis.hive.repository.TenantDetailsRepo
import com.apis.hive.util.ErrorConstants
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DuplicateKeyException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service

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
        val tenantId = getTenantId()
        return dataRepo.findByTenantIdAndKey(tenantId, key) ?: throw HiveException(ErrorConstants.KEY_NOT_EXIST)
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
            if (ex is DuplicateKeyException) {
                throw HiveException(ErrorConstants.KEY_ALREADY_EXIST)
            } else throw ex
        }
    }

    fun deleteDataByKey(key: String) {
        val result = findDataByKey(key)
        if (result != null) {
            val tenantId = tenantService.getTenantId()
            dataRepo.deleteById(TenantKey(tenantId,key))
        } else {
            throw HiveException(ErrorConstants.KEY_NOT_EXIST)
        }
    }

    private fun validateTenantLimit(currentAdditionCount:Int) {
        try {
            val tenantId = getTenantId()
            val tenantLimit = tenantDetailsRepo.findById(tenantId).get().storageSize!!
            val existingCount = dataRepo.getValidKeyCount(tenantId)
            if( tenantLimit < existingCount+currentAdditionCount ){
                throw HiveException(ErrorConstants.DATA_LIMIT_EXCEEDED_FOR_TENANT)
            }
        } catch (ex: Exception){
            logger.error("Exception while checking tenant data limit",ex)
            throw ex
        }
    }

    private fun getTenantId(): Long {
        val tenantAuth = SecurityContextHolder.getContext().authentication as UsernamePasswordAuthenticationToken
        return tenantAuth.principal as Long
    }

}
