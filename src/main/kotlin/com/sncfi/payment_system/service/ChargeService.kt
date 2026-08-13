package com.sncfi.payment_system.service

import com.sncfi.payment_system.dto.AssignChargeBatchRequest
import com.sncfi.payment_system.dto.AssignChargeRequest
import com.sncfi.payment_system.entity.Charge
import com.sncfi.payment_system.entity.ChargeStatus
import com.sncfi.payment_system.exception.ResourceNotFoundException
import com.sncfi.payment_system.repository.ChargeRepository
import com.sncfi.payment_system.repository.FeeItemRepository
import com.sncfi.payment_system.repository.StudentRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class ChargeService(
    private val chargeRepository: ChargeRepository,
    private val studentRepository: StudentRepository,
    private val feeItemRepository: FeeItemRepository
) {

    fun outstandingChargesFor(studentId: Long): List<Charge> =
        chargeRepository.findByStudentIdAndStatusIn(
            studentId,
            listOf(ChargeStatus.UNPAID, ChargeStatus.PARTIAL)
        )

    fun allChargesFor(studentId: Long): List<Charge> =
        chargeRepository.findByStudentId(studentId)

    @Transactional
    fun assignCharge(request: AssignChargeRequest): Charge {
        val student = studentRepository.findById(request.studentId)
            .orElseThrow { ResourceNotFoundException("Student ${request.studentId} not found") }
        val feeItem = feeItemRepository.findById(request.feeItemId)
            .orElseThrow { ResourceNotFoundException("FeeItem ${request.feeItemId} not found") }

        val charge = Charge(
            student = student,
            feeItem = feeItem,
            amountDue = request.amountOverride ?: feeItem.defaultAmount,
            dueDate = request.dueDate
        )
        return chargeRepository.save(charge)
    }

    /** Bills the same fee to an entire grade/class in one call. */
    @Transactional
    fun assignChargeToMany(request: AssignChargeBatchRequest): List<Charge> =
        request.studentIds.map { studentId ->
            assignCharge(
                AssignChargeRequest(
                    studentId = studentId,
                    feeItemId = request.feeItemId,
                    amountOverride = request.amountOverride,
                    dueDate = request.dueDate
                )
            )
        }
}