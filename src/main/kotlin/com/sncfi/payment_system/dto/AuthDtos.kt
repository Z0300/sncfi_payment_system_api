package com.sncfi.payment_system.dto

import com.sncfi.payment_system.entity.Role
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size

data class RegisterParentRequest(
    @field:NotBlank @field:Size(min = 3, max = 50)
    val username: String,

    @field:NotBlank @field:Email
    val email: String,

    @field:NotBlank @field:Size(min = 8)
    val password: String,

    val contactNumber: String? = null
)

data class LoginRequest(
    @field:NotBlank val username: String,
    @field:NotBlank val password: String
)

data class LoginResponse(
    val token: String,
    val username: String,
    val role: Role,
    val expiresInMs: Long
)