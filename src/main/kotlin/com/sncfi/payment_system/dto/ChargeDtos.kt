package com.sncfi.payment_system.dto

import com.sncfi.payment_system.entity.Charge
import com.sncfi.payment_system.entity.ChargeStatus
import jakarta.validation.constraints.DecimalMin
import jakarta.validation.constraints.NotNull
import java.math.BigDecimal
import java.time.LocalDate

data class AssignChargeRequest(
    @field:NotNull val studentId: Long,
    @field:NotNull val feeItemId: Long,

    // Optional — falls back to FeeItem.defaultAmount if omitted.
    @field:DecimalMin("0.00")
    val amountOverride: BigDecimal? = null,

    val dueDate: LocalDate? = null
)

/** Bills the same fee to an entire grade/class in one call. */
data class AssignChargeBatchRequest(
    @field:NotNull val studentIds: List<Long>,
    @field:NotNull val feeItemId: Long,
    val amountOverride: BigDecimal? = null,
    val dueDate: LocalDate? = null
)

data class ChargeResponse(
    val id: Long,
    val studentId: Long,
    val feeItemId: Long,
    val feeItemName: String,
    val amountDue: BigDecimal,
    val amountPaid: BigDecimal,
    val remainingBalance: BigDecimal,
    val status: ChargeStatus,
    val dueDate: LocalDate?
)

fun Charge.toResponse() = ChargeResponse(
    id = id!!,
    studentId = student.id!!,
    feeItemId = feeItem.id!!,
    feeItemName = feeItem.name,
    amountDue = amountDue,
    amountPaid = amountPaid,
    remainingBalance = remainingBalance,
    status = status,
    dueDate = dueDate
)