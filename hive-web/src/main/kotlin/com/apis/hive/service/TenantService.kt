package com.apis.hive.service

import com.apis.hive.util.JwtUtil
import net.mguenther.idem.flake.Flake64L
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class TenantService {

    @Autowired
    private lateinit var hiveAPIService: HiveAPIService

    @Autowired
    private lateinit var idGen: Flake64L

    @Autowired
    private lateinit var jwtUtil: JwtUtil

    fun createTenant(): Map<String, Any?>? {
        val tenantId = idGen.nextId()
        val responseMap = hiveAPIService.sendPostRequest(tenantId.toString())
        val resultMap = mutableMapOf<String, Any?>()
        if (responseMap?.get("status") != "SUCCESS") {
            resultMap["status"] = "FAILURE"
        } else {
            resultMap["status"] = "SUCCESS"
            resultMap["tenantId"] = tenantId
            resultMap["token"] = jwtUtil.generateToken(tenantId)
        }
        return resultMap
    }
}
