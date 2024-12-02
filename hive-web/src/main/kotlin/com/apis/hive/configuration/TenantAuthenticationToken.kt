package com.apis.hive.configuration

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails

class TenantAuthenticationToken(principal: UserDetails, tenantId: Long) : AbstractAuthenticationToken(principal.authorities) {
    private val principal: UserDetails
    var tenantId: Long

    init {
        this.principal = principal
        this.tenantId = tenantId
        isAuthenticated = true
    }

    override fun getCredentials(): Any {
        return principal
    }

    override fun getPrincipal(): Any {
        return principal
    }
}

