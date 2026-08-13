package com.sncfi.payment_system.security

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtService(
    @Value($$"${app.jwt.secret}") secret: String,
    @Value($$"${app.jwt.expiration-ms}") private val expirationMs: Long
) {
    private val key: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray())

    fun generateToken(userDetails: CustomUserDetails): String {
        val now = Date()
        val expiry = Date(now.time + expirationMs)

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
            val claims = parseClaims(token)
            claims.expiration.after(Date())
        } catch (ex: Exception) {
            false
        }

    private fun parseClaims(token: String): Claims =
        Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload

    val expirationMillis: Long get() = expirationMs
}