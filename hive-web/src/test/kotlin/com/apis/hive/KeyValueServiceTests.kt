package com.apis.hive

import com.apis.hive.entity.Data
import com.apis.hive.entity.TenantDetails
import com.apis.hive.entity.TenantKey
import com.apis.hive.exception.KeyAlreadyExistsException
import com.apis.hive.exception.KeyNotFoundException
import com.apis.hive.exception.StorageLimitExceededException
import com.apis.hive.repository.DataRepo
import com.apis.hive.repository.TenantDetailsRepo
import com.apis.hive.service.KeyValueDataService
import com.apis.hive.service.TenantService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
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
import org.springframework.boot.test.context.SpringBootTest
import java.util.*


@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class KeyValueServiceTests {

    @Mock
    private lateinit var dataRepo: DataRepo

    @Mock
    private lateinit var tenantDetailsRepo: TenantDetailsRepo

    @Mock
    private lateinit var tenantService: TenantService

    @InjectMocks
    private lateinit var keyValueDataService: KeyValueDataService

    private lateinit var mockValue : MutableMap<String,Any?>

    init {
        MockitoAnnotations.openMocks(this)
        mockValue = mutableMapOf("testObj" to "stringObj", "testNum" to 123123L)
    }

    @Test
    fun `findDataByKey should return data when key exists`() {
        val tenantId = 1L
        val key = "testKey"
        val data = Data(TenantKey(tenantId, key), mockValue, null)
        `when`(dataRepo.findByTenantIdAndKey(anyLong(), anyString(), anyLong())).thenReturn(data)
        `when`(tenantService.getTenantId()).thenReturn(tenantId)
        val result = keyValueDataService.findDataByKey(key)
        assertNotNull(result)
        assertEquals(data, result)
        verify(dataRepo).findByTenantIdAndKey(anyLong(), anyString(), anyLong())
    }

    @Test
    fun `findDataByKey should throw KeyNotFoundException when key does not exist`() {
        val tenantId = 1L
        val key = "missingKey"
        `when`(dataRepo.findByTenantIdAndKey(anyLong(), anyString(), anyLong())).thenReturn(null)
        `when`(tenantService.getTenantId()).thenReturn(tenantId)
        val exception = assertThrows<KeyNotFoundException> {
            keyValueDataService.findDataByKey(key)
        }
        assertEquals("$key not found", exception.message)
    }

    @Test
    fun `bulkSaveData should save all data when input is valid`() {
        val tenantId = 1L
        val inputData = listOf(
            mapOf("key" to "key1", "value" to mockValue, "ttl" to null),
            mapOf("key" to "key2", "value" to mockValue, "ttl" to 10)
        )
        `when`(tenantService.getTenantId()).thenReturn(tenantId)
        `when`(keyValueDataService.validateTenantLimit(inputData.size)).thenReturn(Unit)
        `when`(dataRepo.saveAll(ArgumentMatchers.anyList())).thenReturn(listOf())
        Mockito.doNothing().`when`(dataRepo).flush()
        keyValueDataService.bulkSaveData(inputData)
        verify(dataRepo).saveAll(ArgumentMatchers.anyList())
        verify(dataRepo).flush()
    }

    @Test
    fun `bulkSaveData should throw KeyAlreadyExistsException on duplicate key`() {
        val tenantId = 1L
        val inputData = listOf(
            mapOf("key" to "key1", "value" to mockValue, "ttl" to null)
        )
        `when`(tenantService.getTenantId()).thenReturn(tenantId)
        `when`(dataRepo.saveAll(ArgumentMatchers.anyList())).thenThrow(RuntimeException("Duplicate entry"))
        val exception = assertThrows<KeyAlreadyExistsException> {
            keyValueDataService.bulkSaveData(inputData)
        }
        assertEquals("duplicate key found", exception.message)
    }

    @Test
    fun `deleteDataByKey should delete data when key exists`() {
        val tenantId = 1L
        val key = "testKey"
        val data = Data(TenantKey(tenantId, key), mockValue, null)
        `when`(dataRepo.findByTenantIdAndKey(anyLong(), anyString(), anyLong())).thenReturn(data)
        `when`(tenantService.getTenantId()).thenReturn(tenantId)
        keyValueDataService.deleteDataByKey(key)
        verify(dataRepo).deleteById(TenantKey(tenantId, key))
    }

    @Test
    fun `deleteDataByKey should throw KeyNotFoundException when key does not exist`() {
        val tenantId = 1L
        val key = "missingKey"
        `when`(dataRepo.findByTenantIdAndKey(anyLong(), anyString(), anyLong())).thenReturn(null)
        `when`(tenantService.getTenantId()).thenReturn(tenantId)
        val exception = assertThrows<KeyNotFoundException> {
            keyValueDataService.deleteDataByKey(key)
        }
        assertEquals("missingKey", exception.message)
    }

    @Test
    fun `validateTenantLimit should throw StorageLimitExceededException when tenant exceeds limit`() {
        val tenantId = 1L
        val currentAdditionCount = 6
        val existingCount = 95
        val tenantLimit = 100
        `when`(tenantService.getTenantId()).thenReturn(tenantId)
        `when`(tenantDetailsRepo.findById(tenantId)).thenReturn(
            Optional.of(TenantDetails(tenantId, tenantLimit))
        )
        `when`(dataRepo.getValidKeyCount(tenantId)).thenReturn(existingCount)
        val exception = assertThrows<StorageLimitExceededException> {
            keyValueDataService.validateTenantLimit(currentAdditionCount)
        }
        assertEquals("existing count 95 current addition 5", exception.message)
    }

}
