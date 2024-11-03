package com.apis.hive.util

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.util.*
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component

@Component
class JwtUtil {

    @Value("\${app.hive.jwt.secret}")
    private val jwtSecret: String? = null

    @Value("\${app.hive.jwt.tokenExpiryInMinutes}")
    private val jwtTokenExpiryInMinutes: String? = null

    fun extractAllClaims(token: String?): Claims {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody()
    }

    fun extractTenantId(token: String?): String? {
        return try {
            extractAllClaims(token).get(AppConstant.TENANT_ID, String::class.java)
        } catch (ex: Exception) {
            null
        }
    }

    fun generateToken(tenantId: Long): String {
        val expirationDate = Date(System.currentTimeMillis() + jwtTokenExpiryInMinutes?.toInt()!! * 60 * 1000)
        return Jwts.builder().claim(AppConstant.TENANT_ID, tenantId.toString())
            .setIssuedAt(Date())
            .setExpiration(expirationDate)
            .signWith(SignatureAlgorithm.HS256, jwtSecret)
            .compact()
    }

    fun isTokenExpired(token: String?): Boolean {
        val claims: Claims = extractAllClaims(token)
        return claims.expiration.before(Date())
    }
}
