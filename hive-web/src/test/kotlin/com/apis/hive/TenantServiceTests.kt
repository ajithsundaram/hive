package com.apis.hive

import com.apis.hive.entity.TenantDetails
import com.apis.hive.exception.ServerErrorException
import com.apis.hive.repository.TenantDetailsRepo
import com.apis.hive.service.TenantService
import com.apis.hive.util.AppConstant
import com.apis.hive.util.ErrorConstants
import com.apis.hive.util.JwtUtil
import net.mguenther.idem.flake.Flake64L
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import org.mockito.ArgumentMatchers.any
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.MockitoAnnotations
import org.springframework.boot.test.context.SpringBootTest
import java.util.*

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TenantServiceTests {

    @Mock
    private lateinit var tenantDetailsRepo: TenantDetailsRepo

    @Mock
    private lateinit var idGen: Flake64L

    @Mock
    private lateinit var jwtUtil: JwtUtil

    @InjectMocks
    private lateinit var tenantService: TenantService

    init {
        MockitoAnnotations.openMocks(this)
    }

    @Test
    fun `test createTenant successfully creates a tenant and generates token`() {
        val input = mapOf(AppConstant.STORAGE_SIZE to "50")
        val tenantId = 12345L
        val jwtToken = "mock-jwt-token"
        val expectedToken = mapOf(
            AppConstant.TENANT_ID to tenantId.toString(),
            AppConstant.TOKEN to jwtToken
        )

        `when`(idGen.nextId()).thenReturn(tenantId)
        `when`(jwtUtil.generateToken(tenantId)).thenReturn(jwtToken)
        val mockTenantDetails = TenantDetails()
        `when`(tenantDetailsRepo.saveAndFlush(any(TenantDetails::class.java))).thenReturn(mockTenantDetails)
        val result = tenantService.createTenant(input)
        assertNotNull(result)
        assertEquals(expectedToken, result)
        verify(tenantDetailsRepo).saveAndFlush(any(TenantDetails::class.java))
        verify(jwtUtil).generateToken(tenantId)
    }

    @Test
    fun `test validateAndGetToken successfully validates tenant and generates token`() {
        val tenantId = 12345L
        val jwtToken = "mock-jwt-token"
        val expectedToken = mapOf(
            AppConstant.TENANT_ID to tenantId.toString(),
            AppConstant.TOKEN to jwtToken
        )
        val mockTenantDetails = TenantDetails().apply { this.tenantId = tenantId }
        `when`(tenantDetailsRepo.findById(tenantId)).thenReturn(Optional.of(mockTenantDetails))
        `when`(jwtUtil.generateToken(tenantId)).thenReturn(jwtToken)
        val result = tenantService.validateAndGetToken(tenantId)
        assertNotNull(result)
        assertEquals(expectedToken, result)
        verify(tenantDetailsRepo).findById(tenantId)
        verify(jwtUtil).generateToken(tenantId)
    }

    @Test
    fun `test validateAndGetToken throws exception for invalid tenant`() {
        val tenantId = 12345L
        `when`(tenantDetailsRepo.findById(tenantId)).thenReturn(Optional.empty())
        val exception = assertThrows(ServerErrorException::class.java) {
            tenantService.validateAndGetToken(tenantId)
        }
        assertEquals(ErrorConstants.INVALID_TENANT.message + tenantId, exception.message)
        verify(tenantDetailsRepo).findById(tenantId)
    }
}

