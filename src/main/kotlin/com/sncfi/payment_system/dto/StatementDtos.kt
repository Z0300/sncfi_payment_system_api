package com.sncfi.payment_system.dto

import com.sncfi.payment_system.dto.ChargeResponse
import com.sncfi.payment_system.dto.PaymentResponse
import java.math.BigDecimal

data class StatementResponse(
    val studentId: Long,
    val studentName: String,
    val charges: List<ChargeResponse>,
    val payments: List<PaymentResponse>,
    val runningBalance: BigDecimal
)