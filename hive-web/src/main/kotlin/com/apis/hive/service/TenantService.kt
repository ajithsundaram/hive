package com.apis.hive.service

import com.apis.hive.entity.TenantDetails
import com.apis.hive.exception.ServerErrorException
import com.apis.hive.repository.TenantDetailsRepo
import com.apis.hive.util.AppConstant
import com.apis.hive.util.ErrorConstants
import com.apis.hive.util.JwtUtil
import net.mguenther.idem.flake.Flake64L
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class TenantService {
    @Autowired
    private lateinit var idGen: Flake64L
    @Value("\${app.hive.tenant.default.storageLimit}")
    private val tenantDefaultStorageLimit: String? = null
    @Autowired
    private lateinit var tenantDetailsRepo: TenantDetailsRepo
    @Autowired
    private lateinit var jwtUtil: JwtUtil

    @Transactional
    fun createTenant(input: Map<String,Any?>): Map<String, Any?>? {
        val tenantId = idGen.nextId()
        val tenantDetails = TenantDetails().apply {
            this.tenantId = tenantId
            this.storageSize = input[AppConstant.STORAGE_SIZE]?.toString()?.toInt() ?: tenantDefaultStorageLimit!!.toInt()
            }
            tenantDetailsRepo.saveAndFlush(tenantDetails)
            return generateToken(tenantId)
    }

    fun validateAndGetToken(tenantId: Long): Map<String, Any?>? {
        tenantDetailsRepo.findById(tenantId).orElseThrow { ServerErrorException(ErrorConstants.INVALID_TENANT.message + tenantId) }
        return generateToken(tenantId)
    }

    private fun generateToken(tenantId: Long): Map<String, Any?>  {
        val jwtToken = jwtUtil.generateToken(tenantId)
        return mapOf(AppConstant.TENANT_ID to tenantId , AppConstant.TOKEN to jwtToken)
    }

    fun getTenantId(): Long {
        return SecurityContextHolder.getContext().authentication.principal as Long
    }
}
