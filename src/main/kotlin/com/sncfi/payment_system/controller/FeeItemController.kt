package com.sncfi.payment_system.controller

import com.sncfi.payment_system.dto.CreateFeeItemRequest
import com.sncfi.payment_system.dto.FeeItemResponse
import com.sncfi.payment_system.dto.toResponse
import com.sncfi.payment_system.service.FeeItemService
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/fee-items")
class FeeItemController(
    private val feeItemService: FeeItemService
) {

    @PreAuthorize("hasAnyRole('CASHIER', 'ADMIN')")
    @GetMapping
    fun getAll(): List<FeeItemResponse> =
        feeItemService.getAll().map { it.toResponse() }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    fun create(@Valid @RequestBody request: CreateFeeItemRequest): FeeItemResponse =
        feeItemService.create(request).toResponse()
}