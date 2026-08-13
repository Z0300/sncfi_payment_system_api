package com.sncfi.payment_system.security

import com.sncfi.payment_system.entity.Role
import com.sncfi.payment_system.entity.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

/**
 * Wraps our User entity for Spring Security. Carries userId and role
 * directly so services (e.g. ParentAccessService, PaymentService) can
 * read them off the SecurityContext without a repository round-trip.
 */
class CustomUserDetails(
    val userId: Long,
    private val username: String,
    private val passwordHash: String,
    val role: Role
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf(SimpleGrantedAuthority("ROLE_$role"))

    override fun getPassword(): String = passwordHash
    override fun getUsername(): String = username
    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean = true
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true

    companion object {
        fun from(user: User) = CustomUserDetails(
            userId = user.id!!,
            username = user.username,
            passwordHash = user.passwordHash,
            role = user.role
        )
    }
}