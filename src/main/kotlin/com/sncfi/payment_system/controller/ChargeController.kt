package com.sncfi.payment_system.controller

import com.sncfi.payment_system.dto.AssignChargeBatchRequest
import com.sncfi.payment_system.dto.AssignChargeRequest
import com.sncfi.payment_system.dto.ChargeResponse
import com.sncfi.payment_system.dto.toResponse
import com.sncfi.payment_system.service.ChargeService
import jakarta.validation.Valid
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/charges")
class ChargeController(
    private val chargeService: ChargeService
) {

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    fun assign(@Valid @RequestBody request: AssignChargeRequest): ChargeResponse =
        chargeService.assignCharge(request).toResponse()

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/batch")
    fun assignBatch(@Valid @RequestBody request: AssignChargeBatchRequest): List<ChargeResponse> =
        chargeService.assignChargeToMany(request).map { it.toResponse() }
}