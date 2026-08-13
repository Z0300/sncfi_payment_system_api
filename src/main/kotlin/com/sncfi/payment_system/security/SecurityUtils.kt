package com.sncfi.payment_system.security

import org.springframework.security.core.context.SecurityContextHolder

/**
 * Central place to read "who's making this request" — used by
 * ParentAccessService for scoping and by PaymentService to record
 * who processed a payment.
 */
object SecurityUtils {

    fun currentUserDetails(): CustomUserDetails? =
        SecurityContextHolder.getContext().authentication?.principal as? CustomUserDetails

    fun currentUserId(): Long? = currentUserDetails()?.userId

    fun currentUsername(): String? = currentUserDetails()?.username
}