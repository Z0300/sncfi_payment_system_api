package com.sncfi.payment_system.security

import com.sncfi.payment_system.config.JwtProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtService(private val jwtProperties: JwtProperties) {
    private val key: SecretKey = Keys.hmacShaKeyFor(jwtProperties.secret.toByteArray())

    fun generateAccessToken(userDetails: CustomUserDetails): String {
        val now = Date()
        val expiry = Date(now.time + jwtProperties.accessTokenExpirationMs)

        return Jwts.builder()
            .subject(userDetails.username)
            .claim("userId", userDetails.userId)
            .claim("role", userDetails.role.name)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(key)
            .compact()
    }

    fun extractUsername(token: String): String = parseClaims(token).subject

    fun isTokenValid(token: String): Boolean =
        try {
            parseClaims(token).expiration.after(Date())
        } catch (ex: Exception) {
            false
        }

    private fun parseClaims(token: String): Claims =
        Jwts.parser().verifyWith(key).build().parseSignedClaims(token).payload

    val accessTokenExpirationMillis: Long get() = jwtProperties.accessTokenExpirationMs
}