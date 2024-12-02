package com.apis.hive.controller

import com.apis.hive.exception.HiveException
import com.apis.hive.service.TenantService
import com.apis.hive.util.AppConstant
import com.apis.hive.util.ErrorConstants
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Component
@RestController
@RequestMapping("/tenant")
class TenantController {
    @Autowired
    private lateinit var tenantService: TenantService
    @PostMapping("/create")
    fun createTenant(@RequestBody body: Map<String, Any?>): ResponseEntity<Any?> {
        val responseMap = tenantService.createTenant(body)
        return ResponseEntity.ok().body(responseMap)
    }

    @PostMapping("/login")
    fun loginTenant(@RequestBody body: Map<String, Any?>): ResponseEntity<Any?> {
        val tenantId = body[AppConstant.TENANT_ID]?.toString()?.toLong() ?: throw HiveException(ErrorConstants.INVALID_INPUT)
        val responseMap = tenantService.validateAndGetToken(tenantId)
        return ResponseEntity.ok().body(responseMap)
    }
}
