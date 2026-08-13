package com.sncfi.payment_system.service

import com.sncfi.payment_system.dto.StatementResponse
import com.sncfi.payment_system.dto.toResponse
import com.sncfi.payment_system.exception.ResourceNotFoundException
import com.sncfi.payment_system.repository.PaymentRepository
import com.sncfi.payment_system.repository.StudentRepository
import org.springframework.stereotype.Service
import java.math.BigDecimal

@Service
class StatementService(
    private val studentRepository: StudentRepository,
    private val paymentRepository: PaymentRepository,
    private val chargeService: ChargeService
) {

    /**
     * Always computed live from Charge + Payment — never a stored snapshot,
     * per the guide: an SOA should reflect current data every time it's viewed.
     */
    fun getStatement(studentId: Long): StatementResponse {
        val student = studentRepository.findById(studentId)
            .orElseThrow { ResourceNotFoundException("Student $studentId not found") }

        val charges = chargeService.allChargesFor(studentId)
        val payments = paymentRepository.findByStudentIdOrderByCreatedAtDesc(studentId)

        val runningBalance = charges.fold(BigDecimal.ZERO) { acc, charge ->
            acc.add(charge.remainingBalance)
        }

        return StatementResponse(
            studentId = student.id!!,
            studentName = student.name,
            charges = charges.map { it.toResponse() },
            payments = payments.map { it.toResponse() },
            runningBalance = runningBalance
        )
    }
}