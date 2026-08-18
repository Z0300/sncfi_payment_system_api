package com.sncfi.payment_system.controller

import com.sncfi.payment_system.dto.CompletePaymentRequest
import com.sncfi.payment_system.dto.PaymentResponse
import com.sncfi.payment_system.dto.toResponse
import com.sncfi.payment_system.exception.ResourceNotFoundException
import com.sncfi.payment_system.repository.PaymentRepository
import com.sncfi.payment_system.security.SecurityUtils
import com.sncfi.payment_system.service.PaymentService
import com.sncfi.payment_system.service.UserService
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/payments")
class PaymentController(
    private val paymentService: PaymentService,
    private val paymentRepository: PaymentRepository,
    private val userService: UserService
) {

    @PreAuthorize("hasAnyRole('CASHIER', 'ADMIN')")
    @PostMapping
    fun completePayment(@Valid @RequestBody request: CompletePaymentRequest): PaymentResponse {
        val processedBy = userService.getById(SecurityUtils.currentUserId()!!)
        return paymentService.completePayment(request, processedBy).toResponse()
    }

    @PreAuthorize("hasAnyRole('CASHIER', 'ADMIN')")
    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): PaymentResponse =
        paymentRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Payment $id not found") }
            .toResponse()
}