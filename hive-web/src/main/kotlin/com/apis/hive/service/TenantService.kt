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

    fun createTenant(input: Map<String,String?>): Map<String, String?>? {
        val tenantId = idGen.nextId()
        val tenantDetails = TenantDetails().apply {
            this.tenantId = tenantId
            this.storageSize = input[AppConstant.STORAGE_SIZE]?.toInt() ?: tenantDefaultStorageLimit!!.toInt()
            }
            tenantDetailsRepo.saveAndFlush(tenantDetails)
            return generateToken(tenantId)
    }

    fun validateAndGetToken(tenantId: Long): Map<String, String?>? {
        tenantDetailsRepo.findById(tenantId).orElseThrow { ServerErrorException(ErrorConstants.INVALID_TENANT.message + tenantId) }
        return generateToken(tenantId)
    }

    private fun generateToken(tenantId: Long): Map<String, String?>  {
        val jwtToken = jwtUtil.generateToken(tenantId)
        return mapOf(AppConstant.TENANT_ID to tenantId.toString() , AppConstant.TOKEN to jwtToken)
    }

    fun getTenantId(): Long {
        return SecurityContextHolder.getContext().authentication.principal as Long
    }
}
