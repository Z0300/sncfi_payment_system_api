package com.sncfi.payment_system.controller

import com.sncfi.payment_system.dto.LoginRequest
import com.sncfi.payment_system.dto.LoginResponse
import com.sncfi.payment_system.dto.RegisterParentRequest
import com.sncfi.payment_system.entity.User
import com.sncfi.payment_system.repository.UserRepository
import com.sncfi.payment_system.security.CustomUserDetails
import com.sncfi.payment_system.security.JwtService
import com.sncfi.payment_system.security.RefreshTokenService
import com.sncfi.payment_system.service.UserService
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.time.Duration

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val userService: UserService,
    private val userRepository: UserRepository,
    private val authenticationManager: AuthenticationManager,
    private val jwtService: JwtService,
    private val refreshTokenService: RefreshTokenService,
    @Value($$"${app.cookie.secure}") private val cookieSecure: Boolean
) {

    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterParentRequest, response: HttpServletResponse): LoginResponse {
        val user = userService.registerParent(
            request.username, request.email, request.password, request.contactNumber
        )
        return issueTokens(user, response)
    }

    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest, response: HttpServletResponse): LoginResponse {
        val authentication = authenticationManager.authenticate(
            UsernamePasswordAuthenticationToken(request.username, request.password)
        )
        val userDetails = authentication.principal as CustomUserDetails
        val user = userRepository.findById(userDetails.userId).orElseThrow()
        return issueTokens(user, response)
    }

    @PostMapping("/refresh")
    fun refresh(
        @CookieValue("refreshToken") refreshToken: String,
        response: HttpServletResponse
    ): LoginResponse {
        val (user, newRefreshToken) = refreshTokenService.rotate(refreshToken)
        setRefreshCookie(response, newRefreshToken)
        val accessToken = jwtService.generateAccessToken(CustomUserDetails.from(user))
        return LoginResponse(accessToken, user.username, user.role, jwtService.accessTokenExpirationMillis)
    }

    @PostMapping("/logout")
    fun logout(
        @CookieValue(value = "refreshToken", required = false) refreshToken: String?,
        response: HttpServletResponse
    ): ResponseEntity<Void> {
        refreshToken?.let { refreshTokenService.revoke(it) }
        clearRefreshCookie(response)
        return ResponseEntity.noContent().build()
    }

    private fun issueTokens(user: User, response: HttpServletResponse): LoginResponse {
        val userDetails = CustomUserDetails.from(user)
        val accessToken = jwtService.generateAccessToken(userDetails)
        val refreshToken = refreshTokenService.issue(user)
        setRefreshCookie(response, refreshToken)
        return LoginResponse(accessToken, user.username, user.role, jwtService.accessTokenExpirationMillis)
    }

    private fun setRefreshCookie(response: HttpServletResponse, token: String) {
        val cookie = ResponseCookie.from("refreshToken", token)
            .httpOnly(true)
            .secure(cookieSecure)       // false for local http, true once you're on https
            .path("/api/auth")          // only sent back to auth endpoints, not every request
            .sameSite("Lax")
            .maxAge(Duration.ofDays(7))
            .build()
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
    }

    private fun clearRefreshCookie(response: HttpServletResponse) {
        val cookie = ResponseCookie.from("refreshToken", "")
            .httpOnly(true).secure(cookieSecure).path("/api/auth").maxAge(0).build()
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString())
    }
}