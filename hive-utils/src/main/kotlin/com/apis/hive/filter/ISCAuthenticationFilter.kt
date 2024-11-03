package com.apis.hive.filter

import com.apis.hive.util.AppConstant
import com.apis.hive.util.TenantContext
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.core.annotation.Order
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
@Order(1)
class ISCAuthenticationFilter : OncePerRequestFilter() {

    @Value("\${app.hive.interService.apiKey}")
    private lateinit var iscToken: String

    val log = LoggerFactory.getLogger(this::class.java)

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            if (AppConstant.ISC_ENDPOINTS.contains(request.requestURI) && request.getHeader(AUTHORIZATION) != null) {
                val token = extractIscToken(request.getHeader(AUTHORIZATION)!!)
                if (iscToken != token) {
                    response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied")
                    return
                }
            }
            filterChain.doFilter(request, response)
        } catch (ex: Exception) {
            log.error("Exception while processing Request in isc filter",ex)
        } finally {
            TenantContext.remove()
        }
        println("Exit ISCAuthenticationFilter")
    }

    private fun extractIscToken(autHeaderValue: String): String {
        return autHeaderValue.substring(7)
    }
}
