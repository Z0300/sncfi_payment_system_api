package com.sncfi.payment_system.security

import com.sncfi.payment_system.config.JwtProperties
import com.sncfi.payment_system.entity.RefreshToken
import com.sncfi.payment_system.entity.User
import com.sncfi.payment_system.exception.InvalidRefreshTokenException
import com.sncfi.payment_system.repository.RefreshTokenRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.security.MessageDigest
import java.security.SecureRandom
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.util.Base64

@Service
class RefreshTokenService(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val jwtProperties: JwtProperties
) {
    private val secureRandom = SecureRandom()

    /** Issues a new raw refresh token, stores only its hash. */
    fun issue(user: User): String {
        val bytes = ByteArray(64)
        secureRandom.nextBytes(bytes)
        val rawToken = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes)

        refreshTokenRepository.save(
            RefreshToken(
                user = user,
                tokenHash = sha256(rawToken),
                expiresAt = LocalDateTime.now().plus(jwtProperties.refreshTokenExpirationMs, ChronoUnit.MILLIS)
            )
        )
        return rawToken
    }

    /**
     * Rotation: the presented token is immediately revoked and a new one
     * issued, whether or not the presented one turns out to be valid on
     * reuse. This is what makes stolen-refresh-token reuse detectable —
     * if a revoked token is ever presented again, that's a signal the
     * token was compromised (a real system would revoke the whole
     * family here; noting it as a known next step, not implementing yet).
     */
    @Transactional
    fun rotate(rawToken: String): Pair<User, String> {
        val existing = refreshTokenRepository.findByTokenHash(sha256(rawToken))
            ?: throw InvalidRefreshTokenException("Invalid refresh token")

        if (existing.revoked || existing.expiresAt.isBefore(LocalDateTime.now())) {
            throw InvalidRefreshTokenException("Refresh token expired or already used")
        }

        existing.revoked = true
        refreshTokenRepository.save(existing)

        val newToken = issue(existing.user)
        return existing.user to newToken
    }

    fun revoke(rawToken: String) {
        refreshTokenRepository.findByTokenHash(sha256(rawToken))?.let {
            it.revoked = true
            refreshTokenRepository.save(it)
        }
    }

    private fun sha256(raw: String): String =
        MessageDigest.getInstance("SHA-256")
            .digest(raw.toByteArray())
            .joinToString("") { "%02x".format(it) }
}