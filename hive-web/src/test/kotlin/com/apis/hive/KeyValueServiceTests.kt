package com.apis.hive

import com.apis.hive.entity.Data
import com.apis.hive.entity.TenantDetails
import com.apis.hive.entity.TenantKey
import com.apis.hive.exception.KeyAlreadyExistsException
import com.apis.hive.exception.KeyNotFoundException
import com.apis.hive.exception.StorageLimitExceededException
import com.apis.hive.migrationHandler.SchemaMigrationHandler
import com.apis.hive.repository.DataRepo
import com.apis.hive.repository.TenantDetailsRepo
import com.apis.hive.service.KeyValueDataService
import com.apis.hive.service.TenantService
import com.apis.hive.util.AppConstant
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.MethodOrderer
import org.junit.jupiter.api.Order
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.junit.jupiter.api.TestMethodOrder
import org.junit.jupiter.api.assertThrows
import org.mockito.ArgumentMatchers
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.anyLong
import org.mockito.Mockito.anyString
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.test.context.TestExecutionListeners
import java.util.*

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation::class)
class KeyValueServiceTests(
    @Autowired
    private var keyValueDataService: KeyValueDataService,
    @Autowired
    private var tenantServiceBean: TenantService,
    @Autowired
    private var schemaMigrationHandler: SchemaMigrationHandler
) {
    private var mockValue = mutableMapOf("testObj" to "stringObj", "testNum" to 123123L)
    private var testTenantId1: Long? = null
    private var testTenantId2: Long? = null

    @BeforeAll
    fun initializeTenants() {
        schemaMigrationHandler.migrateSchema()
        testTenantId1 = tenantServiceBean.createTenant(mapOf())?.get(AppConstant.TENANT_ID)?.toLong()!!
        testTenantId2 = tenantServiceBean.createTenant(mapOf(AppConstant.STORAGE_SIZE to "2"))?.get(AppConstant.TENANT_ID)?.toLong()!!
    }

    private fun setTenantScopeForTestSuite(tenantId: Long) {
        SecurityContextHolder.getContext().authentication =  UsernamePasswordAuthenticationToken(tenantId, null, emptyList())
    }

    @Test
    @Order(1)
    fun `findDataByKey should return data when key exists`() {
        setTenantScopeForTestSuite(testTenantId1!!)
        val key = "testKey"
        val keyValueDataInputMap = mutableMapOf(AppConstant.DATA_KEY to key, AppConstant.DATA_VALUE to mockValue as Any?)
        val data = Data(TenantKey(testTenantId1, key), mockValue.toMutableMap(), null)

        keyValueDataService.bulkSaveData(listOf(keyValueDataInputMap))
        val result = keyValueDataService.findDataByKey(key)
        assertNotNull(result)
        assertEquals(data.id?.dataKey, result?.id?.dataKey)
        assertEquals(data.dataValue.hashCode(), result?.dataValue.hashCode())
    }

    @Test
    @Order(2)
    fun `findDataByKey should throw KeyNotFoundException when recordKey does not exist`() {
        setTenantScopeForTestSuite(testTenantId1!!)
        val recordKey = "missingKey"
        val exception = assertThrows<KeyNotFoundException> {
            keyValueDataService.findDataByKey(recordKey)
        }
        assertEquals("$recordKey not found", exception.message)
    }

    @Test
    @Order(3)
    fun `bulkSaveData should save all data when input is valid`() {
        setTenantScopeForTestSuite(testTenantId1!!)
        val inputData = listOf(
            mapOf(AppConstant.DATA_KEY to "key1", AppConstant.DATA_VALUE to mockValue, "ttl" to null),
            mapOf(AppConstant.DATA_KEY to "key2", AppConstant.DATA_VALUE to mockValue, "ttl" to 10)
        )
        keyValueDataService.bulkSaveData(inputData)
        val result1 = keyValueDataService.findDataByKey("key1")
        assertNotNull(result1)
        assertEquals(result1?.id?.dataKey, "key1")
        assertEquals(result1?.dataValue.hashCode(), mockValue.hashCode())
        val result2 = keyValueDataService.findDataByKey("key2")
        assertNotNull(result2)
        assertEquals(result2?.id?.dataKey, "key2")
        assertEquals(result2?.dataValue.hashCode(), mockValue.hashCode())
    }

    @Test
    @Order(4)
    fun `bulkSaveData should throw KeyAlreadyExistsException on duplicate recordKey`() {
        setTenantScopeForTestSuite(testTenantId1!!)
        val inputData = listOf(
            mapOf(AppConstant.DATA_KEY to "uniqueKey1", AppConstant.DATA_VALUE to mockValue, "ttl" to null)
        )
        keyValueDataService.bulkSaveData(inputData)
        val exception = assertThrows<DataIntegrityViolationException> {
            keyValueDataService.bulkSaveData(inputData)
        }
        assert(exception.message?.contains("Unique index or primary key violation") == true)
    }

    @Test
    @Order(5)
    fun `deleteDataByKey should delete data when key exists`() {
        setTenantScopeForTestSuite(testTenantId1!!)
        val inputData = listOf(
            mapOf(AppConstant.DATA_KEY to "keyNeedsToBeDeleted", AppConstant.DATA_VALUE to mockValue, "ttl" to null)
        )
        keyValueDataService.bulkSaveData(inputData)
        keyValueDataService.deleteDataByKey("keyNeedsToBeDeleted")
        assertThrows<KeyNotFoundException> {
            keyValueDataService.findDataByKey("keyNeedsToBeDeleted")
        }
    }

    @Test
    @Order(6)
    fun `validateTenantLimit should throw StorageLimitExceededException when tenant exceeds limit`() {
        setTenantScopeForTestSuite(testTenantId2!!)
        var inputData = listOf(
            mapOf(AppConstant.DATA_KEY to "key1", AppConstant.DATA_VALUE to mockValue, "ttl" to null),
            mapOf(AppConstant.DATA_KEY to "key2", AppConstant.DATA_VALUE to mockValue, "ttl" to 10)
        )
        keyValueDataService.bulkSaveData(inputData)
        inputData = listOf(
            mapOf(AppConstant.DATA_KEY to "key3", AppConstant.DATA_VALUE to mockValue, "ttl" to null)
        )
        assertThrows<StorageLimitExceededException> {
            keyValueDataService.bulkSaveData(inputData)
        }
        inputData = listOf(
            mapOf(AppConstant.DATA_KEY to "key4", AppConstant.DATA_VALUE to mockValue, "ttl" to null),
            mapOf(AppConstant.DATA_KEY to "key5", AppConstant.DATA_VALUE to mockValue, "ttl" to null)
        )
        assertThrows<StorageLimitExceededException> {
            keyValueDataService.bulkSaveData(inputData)
        }
    }

    @Test
    @Order(7)
    fun `check one Tenant's data not accessible in another Tenant`() {
        setTenantScopeForTestSuite(testTenantId1!!)
        val inputData = listOf(
            mapOf(AppConstant.DATA_KEY to "Tenant1_key_1", AppConstant.DATA_VALUE to mockValue, "ttl" to null),
            mapOf(AppConstant.DATA_KEY to "Tenant1_key_2", AppConstant.DATA_VALUE to mockValue, "ttl" to 10)
        )
        keyValueDataService.bulkSaveData(inputData)
        setTenantScopeForTestSuite(testTenantId2!!)
        assertThrows<KeyNotFoundException> {
            keyValueDataService.findDataByKey("Tenant1_key_1")
        }
        assertThrows<KeyNotFoundException> {
            keyValueDataService.findDataByKey("Tenant1_key_2")
        }
    }
    @Test
    @Order(8)
    fun `test ttl null keys can be accessed for life Time`() {
        setTenantScopeForTestSuite(testTenantId1!!)
        val inputData = listOf(
            mapOf(AppConstant.DATA_KEY to "ttl_null_key", AppConstant.DATA_VALUE to mockValue, "ttl" to null),
            mapOf(AppConstant.DATA_KEY to "ttl_5_key", AppConstant.DATA_VALUE to mockValue, "ttl" to 5)
        )
        keyValueDataService.bulkSaveData(inputData)
        Thread.sleep(6 * 1000)
        assertThrows<KeyNotFoundException> {
            keyValueDataService.findDataByKey("ttl_5_key")
        }
        val ttl_null_key =  keyValueDataService.findDataByKey("ttl_null_key")
        assertNotNull(ttl_null_key)
    }
    }
