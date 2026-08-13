package com.sncfi.payment_system.dto

import com.sncfi.payment_system.entity.Payment
import com.sncfi.payment_system.entity.PaymentLine
import com.sncfi.payment_system.entity.PaymentMethod
import jakarta.validation.Valid
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDateTime

data class ChargePaymentLineRequest(
    @field:NotNull val chargeId: Long,

    @field:NotNull @field:DecimalMin("0.01")
    val amount: BigDecimal
)

data class CompletePaymentRequest(
    @field:NotNull val studentId: Long,

    @field:NotEmpty @field:Valid
    val chargeLines: List<ChargePaymentLineRequest>,

    @field:NotNull val paymentMethod: PaymentMethod,
    val amountTendered: BigDecimal? = null
)

data class PaymentLineResponse(
    val lineType: String,
    val chargeId: Long?,
    val feeItemName: String?,
    val inventoryItemId: Long?,
    val inventoryItemName: String?,
    val quantity: Int,
    val amount: BigDecimal
)

fun PaymentLine.toResponse() = PaymentLineResponse(
    lineType = lineType.name,
    chargeId = charge?.id,
    feeItemName = charge?.feeItem?.name,
    inventoryItemId = inventoryItem?.id,
    inventoryItemName = inventoryItem?.name,
    quantity = quantity,
    amount = amount
)

data class PaymentResponse(
    val id: Long,
    val studentId: Long,
    val studentName: String,
    val total: BigDecimal,
    val paymentMethod: PaymentMethod,
    val amountTendered: BigDecimal?,
    val changeDue: BigDecimal?,
    val processedByUsername: String?,
    val createdAt: LocalDateTime?,
    val lines: List<PaymentLineResponse>
)

fun Payment.toResponse() = PaymentResponse(
    id = id!!,
    studentId = student.id!!,
    studentName = student.name,
    total = total,
    paymentMethod = paymentMethod,
    amountTendered = amountTendered,
    changeDue = changeDue,
    processedByUsername = processedByUser?.username,
    createdAt = createdAt,
    lines = lines.map { it.toResponse() }
)