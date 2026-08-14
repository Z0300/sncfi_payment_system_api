package com.sncfi.payment_system.service

import com.sncfi.payment_system.entity.Payment
import com.sncfi.payment_system.entity.Role
import com.sncfi.payment_system.entity.User
import com.sncfi.payment_system.exception.InvalidPaymentException
import com.sncfi.payment_system.exception.ResourceNotFoundException
import com.sncfi.payment_system.repository.PaymentRepository
import com.sncfi.payment_system.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

@Service
class UserService(
    private val userRepository: UserRepository,
    private val paymentRepository: PaymentRepository,
    private val passwordEncoder: PasswordEncoder
) {

    fun getById(id: Long): User =
        userRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("User $id not found") }

    fun findByUsername(username: String): User =
        userRepository.findByUsername(username)
            ?: throw ResourceNotFoundException("No user found: $username")

    fun registerParent(username: String, email: String, rawPassword: String, contactNumber: String?): User {
        if (userRepository.existsByUsername(username)) {
            throw InvalidPaymentException("Username already taken")
        }
        if (userRepository.existsByEmail(email)) {
            throw InvalidPaymentException("Email already registered")
        }
        val user = User(
            username = username,
            email = email,
            passwordHash = passwordEncoder.encode(rawPassword)!!,
            role = Role.PARENT,
            contactNumber = contactNumber
        )
        return userRepository.save(user)
    }

    fun paymentHistoryFor(studentId: Long, pageable: Pageable): Page<Payment> =
        paymentRepository.findByStudentId(studentId, pageable)
}