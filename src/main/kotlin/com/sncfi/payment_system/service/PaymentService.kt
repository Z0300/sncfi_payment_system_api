package com.sncfi.payment_system.service

import com.sncfi.payment_system.dto.CompletePaymentRequest
import com.sncfi.payment_system.entity.Charge
import com.sncfi.payment_system.entity.ChargeStatus
import com.sncfi.payment_system.entity.Payment
import com.sncfi.payment_system.entity.PaymentLine
import com.sncfi.payment_system.entity.User
import com.sncfi.payment_system.exception.InsufficientBalanceException
import com.sncfi.payment_system.exception.InvalidPaymentException
import com.sncfi.payment_system.exception.ResourceNotFoundException
import com.sncfi.payment_system.repository.ChargeRepository
import com.sncfi.payment_system.repository.PaymentRepository
import com.sncfi.payment_system.repository.StudentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal

@Service
class PaymentService(
    private val paymentRepository: PaymentRepository,
    private val chargeRepository: ChargeRepository,
    private val studentRepository: StudentRepository,
    private val smsNotificationService: SmsNotificationService
) {

    /**
     * Completes a payment: validates against current balances, applies
     * the amounts to each Charge, persists Payment + PaymentLines, and
     * fires a best-effort SMS confirmation — all in one DB transaction
     * except the SMS send (see the guide's note: a failed SMS should
     * never roll back a successful payment).
     */
    @Transactional
    fun completePayment(request: CompletePaymentRequest, processedBy: User): Payment {
        if (request.chargeLines.isEmpty()) {
            throw InvalidPaymentException("Payment must include at least one charge line")
        }

        val student = studentRepository.findById(request.studentId)
            .orElseThrow { ResourceNotFoundException("Student ${request.studentId} not found") }

        // Lock in the charges + validate BEFORE mutating anything, so a bad
        // line further down the list doesn't leave earlier charges half-updated.
        val chargesById: Map<Long, Charge> = request.chargeLines.associate { line ->
            val charge = chargeRepository.findById(line.chargeId)
                .orElseThrow { ResourceNotFoundException("Charge ${line.chargeId} not found") }

            if (charge.student.id != student.id) {
                throw InvalidPaymentException("Charge ${charge.id} does not belong to student ${student.id}")
            }
            if (line.amount > charge.remainingBalance) {
                throw InsufficientBalanceException(
                    "Payment of ${line.amount} exceeds remaining balance ${charge.remainingBalance} on charge ${charge.id}"
                )
            }
            line.chargeId to charge
        }

        val total = request.chargeLines.sumOf { it.amount }

        if (request.paymentMethod == com.sncfi.payment_system.entity.PaymentMethod.CASH) {
            val tendered = request.amountTendered
                ?: throw InvalidPaymentException("amountTendered is required for cash payments")
            if (tendered < total) {
                throw InvalidPaymentException("Amount tendered ($tendered) is less than total due ($total)")
            }
        }

        val payment = Payment(
            student = student,
            processedByUser = processedBy,
            total = total,
            paymentMethod = request.paymentMethod,
            amountTendered = request.amountTendered,
            changeDue = request.amountTendered?.subtract(total)
        )

        request.chargeLines.forEach { line ->
            val charge = chargesById.getValue(line.chargeId)

            // Apply payment to the charge's running balance.
            charge.amountPaid = charge.amountPaid.add(line.amount)
            charge.status = when {
                charge.remainingBalance <= BigDecimal.ZERO -> ChargeStatus.PAID
                else -> ChargeStatus.PARTIAL
            }
            chargeRepository.save(charge)

            payment.addLine(PaymentLine.forCharge(payment, charge, line.amount))
        }

        val saved = paymentRepository.save(payment)

        // Best-effort — a failed SMS should never undo a successful payment.
        smsNotificationService.sendPaymentConfirmation(saved)

        return saved
    }
}