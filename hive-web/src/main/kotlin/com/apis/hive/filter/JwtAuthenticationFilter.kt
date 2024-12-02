package com.apis.hive.filter
import com.apis.hive.repository.TenantDetailsRepo
import com.apis.hive.util.AppConstant
import com.apis.hive.util.JwtUtil
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import kotlin.jvm.optionals.getOrNull

@Component
class JwtAuthenticationFilter : OncePerRequestFilter() {
    @Autowired
    private lateinit var tenantDetailsRepo: TenantDetailsRepo
    @Autowired
    private lateinit var jwtUtil: JwtUtil

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ){
        val requestUri = request.requestURI
        if (AppConstant.PUBLIC_ENDPOINTS.any { requestUri.startsWith(it) }) {
            filterChain.doFilter(request, response)
            return
        }

        val token = resolveToken(request)
        if (token != null ) {
            val tenantId = validateToken(token)
            if(tenantId != null) {
                val auth = getAuthentication(tenantId)
                SecurityContextHolder.getContext().authentication = auth
            } else {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Tenant")
            }
        } else response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token")
        filterChain.doFilter(request, response)
    }

    private fun resolveToken(request: HttpServletRequest): String? {
        return request.getHeader("Authorization")?.takeIf { it.startsWith("Bearer ") }?.substring(7)
    }

    private fun validateToken(token: String): Long? {
        val tenantId = jwtUtil.extractTenantId(token)
        return if(tenantId != null) {
            val tenantDetails = tenantDetailsRepo.findById(tenantId.toLong()).getOrNull()
            if (tenantDetails == null) {
                logger.error("Tenant Not found for tenant $tenantId")
                null
            } else tenantId.toLong()
        } else null
    }

    private fun getAuthentication(tenantId: Long): Authentication {
        return UsernamePasswordAuthenticationToken(tenantId, null, emptyList())
    }
}
