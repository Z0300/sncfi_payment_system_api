package com.sncfi.payment_system.controller

import com.sncfi.payment_system.dto.LoginRequest
import com.sncfi.payment_system.dto.LoginResponse
import com.sncfi.payment_system.dto.RegisterParentRequest
import com.sncfi.payment_system.security.CustomUserDetails
import com.sncfi.payment_system.security.JwtService
import com.sncfi.payment_system.service.UserService
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val userService: UserService,
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService
) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterParentRequest): ResponseEntity<LoginResponse> {
        val user = userService.registerParent(
            username = request.username,
            email = request.email,
            rawPassword = request.password,
            contactNumber = request.contactNumber
        )
        val userDetails = CustomUserDetails.from(user)
        val token = jwtService.generateToken(userDetails)

        return ResponseEntity.status(HttpStatus.CREATED).body(
            LoginResponse(token, user.username, user.role, jwtService.expirationMillis)
        )
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): LoginResponse {
        // Throws BadCredentialsException on mismatch — handled by GlobalExceptionHandler.
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.username, request.password)
        )
        val userDetails = authentication.principal as CustomUserDetails
        val token = jwtService.generateToken(userDetails)

        return LoginResponse(token, userDetails.username, userDetails.role, jwtService.expirationMillis)
    }
}