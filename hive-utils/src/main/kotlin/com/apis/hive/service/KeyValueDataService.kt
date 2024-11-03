package com.apis.hive.service

import com.apis.hive.dto.Data
import com.apis.hive.dto.TenantDetails
import com.apis.hive.exception.HiveException
import com.apis.hive.repo.AggregateFunction
import com.apis.hive.repo.QueryConstant
import com.apis.hive.repo.ReadRepository
import com.apis.hive.repo.WriteRepository
import com.apis.hive.repo.resultSetMapper.DataRowMapper
import com.apis.hive.repo.resultSetMapper.TenantDetailsRowMapper
import com.apis.hive.util.AppConstant
import com.apis.hive.util.ErrorConstants
import com.apis.hive.util.HiveUtil
import com.apis.hive.util.TenantContext
import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.convertValue
import org.slf4j.LoggerFactory
import java.util.LinkedList
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DuplicateKeyException
import org.springframework.stereotype.Service

@Service
class KeyValueDataService {
    @Autowired
    private lateinit var readRepository: ReadRepository
    @Autowired
    private lateinit var writeRepository: WriteRepository
    private var dataRowMapper = DataRowMapper()
    private var objectMapper = ObjectMapper()
    private var tenantDetailsMapper = TenantDetailsRowMapper()
    @Autowired
    private lateinit var hiveUtil: HiveUtil

    private var logger = LoggerFactory.getLogger(this::class.java)

    fun findDataByKey(key: String): Data? {
        val condition = "${QueryConstant.KEY_CONDITION} AND ${QueryConstant.TTL_CONDITION}"
        val queryParams = listOf(key,System.currentTimeMillis())
        return readRepository.find(AppConstant.DATA_TABLE, setOf(), condition, queryParams, dataRowMapper)
    }

    fun bulkSaveData(inputData: List<Map<String, Any?>>): Any? {
        validateTenantLimit(inputData.size)
        try {
            val data = objectMapper.convertValue(inputData, object : TypeReference<List<Data>>() {})
            val insertFields = listOf(AppConstant.DATA_ID, AppConstant.DATA_KEY, AppConstant.DATA_VALUE, AppConstant.DATA_TTL)
            return writeRepository.bulkInsertData(AppConstant.DATA_TABLE, data, insertFields)
        } catch (ex: Exception) {
            logger.error("exception while saving data",ex)
            if (ex is DuplicateKeyException) {
                throw HiveException(ErrorConstants.KEY_ALREADY_EXIST)
            } else throw ex
        }
    }

    fun deleteDataByKey(key: String): Int? {
        val result = findDataByKey(key)
        if (result != null) {
           val params = listOf(key)
           return writeRepository.deleteDataByKey(AppConstant.DATA_TABLE, AppConstant.KEY_CONDITION, params)
        } else {
            throw HiveException(ErrorConstants.KEY_NOT_EXIST)
        }
    }

    fun validateTenantLimit(currentAdditionCount:Int) {
        try {
            val tenantId = TenantContext.getTenantId()!!
            val condition = QueryConstant.TTL_CONDITION
            val queryParams = listOf(System.currentTimeMillis())
            val result = readRepository.findAggregate(
                AppConstant.DATA_TABLE,
                AppConstant.DATA_ID,
                AggregateFunction.COUNT,
                condition,
                queryParams
            )
            val tenantDetails = hiveUtil.executeInHiveSchema {
                val tenantCondition = QueryConstant.TENANT_ID_CONDITION
                val tenantQueryParam = listOf(tenantId)
                readRepository.find(
                    AppConstant.TENANT_DETAILS_TABLE,
                    setOf(),
                    tenantCondition,
                    tenantQueryParam,
                    tenantDetailsMapper
                )
            } as TenantDetails?

            if (tenantDetails?.storageSize == null) {
                logger.error("TenantDetails not found for Tenant {}", tenantId)
                throw HiveException(ErrorConstants.INTERNAL_SERVER_ERROR)
            }
            val existingTenantDataCount = result.toString().toIntOrNull()!!
            if (tenantDetails.storageSize < (existingTenantDataCount + currentAdditionCount)) {
                throw HiveException(ErrorConstants.DATA_LIMIT_EXCEEDED_FOR_TENANT)
            }
        } catch (ex: Exception){
            logger.error("Exception while checking tenant data limit",ex)
            throw ex
        }
    }

}
