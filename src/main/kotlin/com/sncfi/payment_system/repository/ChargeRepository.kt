package com.sncfi.payment_system.repository

import com.sncfi.payment_system.entity.Charge
import com.sncfi.payment_system.entity.ChargeStatus
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface ChargeRepository : JpaRepository<Charge, Long> {
    fun findByStudentIdAndStatusIn(studentId: Long, statuses: List<ChargeStatus>): List<Charge>
    fun findByStudentId(studentId: Long): List<Charge>

    // Used by the scheduled due-reminder job later.
    fun findByStatusInAndDueDateLessThanEqual(
        statuses: List<ChargeStatus>,
        date: LocalDate
    ): List<Charge>
}