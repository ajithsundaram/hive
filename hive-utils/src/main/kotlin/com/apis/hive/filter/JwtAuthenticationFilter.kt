package com.apis.hive.filter
import com.apis.hive.repo.HiveDataRepository
import com.apis.hive.util.AppConstant
import com.apis.hive.util.JwtUtil
import com.apis.hive.util.TenantContext
import com.apis.hive.util.TenantInfo
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
@Order(2)
class JwtAuthenticationFilter : OncePerRequestFilter() {
    @Autowired
    private lateinit var jwtUtil: JwtUtil
    @Autowired
    private lateinit var hiveDataRepository: HiveDataRepository

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        println("Entered JwtAuthenticationFilter")
        try {
            if (!AppConstant.PUBLIC_ENDPOINTS.contains(request.requestURI) && !AppConstant.ISC_ENDPOINTS.contains(request.requestURI)) {
                val authorizationHeader = request.getHeader("Authorization")
                if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
                    val token = authorizationHeader.substring(7)
                    val tenantId = jwtUtil.extractTenantId(token)
                    if (tenantId != null && !jwtUtil.isTokenExpired(token)) {
                        println("got tenantId is $tenantId and path is ${request.requestURI}")
                        val tenantDetails = hiveDataRepository.findByTenantId(tenantId.toLong())
                        if (tenantDetails == null) {
                            println("Authentication failure")
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Tenant")
                            return
                        } else {
                            TenantContext.setTenantScopeInfo(TenantInfo(tenantDetails.name, tenantId.toLong()))
                            println("Authenticated")
                            filterChain.doFilter(request, response)
                        }
                    } else {
                        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token")
                        return
                    }
                }
            } else {
                filterChain.doFilter(request, response)
            }
        } catch (ex: Exception) {
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal Error")
            println("exception while jwt filter ${ex.printStackTrace()}")
            return
        } finally {
            println("Exited JwtAuthenticationFilter")
            TenantContext.remove()
        }
    }
}
