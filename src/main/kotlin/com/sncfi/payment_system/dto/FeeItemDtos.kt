package com.sncfi.payment_system.dto

import com.sncfi.payment_system.entity.FeeCategory
import com.sncfi.payment_system.entity.FeeItem
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal

data class CreateFeeItemRequest(
    @field:NotBlank val name: String,

    @field:NotNull @field:DecimalMin("0.00")
    val defaultAmount: BigDecimal,

    @field:NotNull val category: FeeCategory
)

data class FeeItemResponse(
    val id: Long,
    val name: String,
    val defaultAmount: BigDecimal,
    val category: FeeCategory
)

fun FeeItem.toResponse() = FeeItemResponse(
    id = id!!,
    name = name,
    defaultAmount = defaultAmount,
    category = category
)