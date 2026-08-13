package com.sncfi.payment_system.repository

import com.sncfi.payment_system.entity.Charge
import com.sncfi.payment_system.entity.ChargeStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.math.BigDecimal
import java.time.LocalDate

interface ChargeRepository : JpaRepository<Charge, Long> {
    fun findByStudentIdAndStatusIn(studentId: Long, statuses: List<ChargeStatus>): List<Charge>
    fun findByStudentId(studentId: Long): List<Charge>

    // Used by the scheduled due-reminder job later.
    fun findByStatusInAndDueDateLessThanEqual(
        statuses: List<ChargeStatus>,
        date: LocalDate
    ): List<Charge>


    @Query(
        """
        SELECT c.student.id AS studentId, c.student.name AS studentName, c.student.gradeLevel AS gradeLevel,
               COALESCE(SUM(c.amountDue - c.amountPaid), 0) AS totalOutstanding
        FROM Charge c
        WHERE c.status <> com.sncfi.payment_system.entity.ChargeStatus.PAID
        GROUP BY c.student.id, c.student.name, c.student.gradeLevel
        HAVING SUM(c.amountDue - c.amountPaid) > 0
        """
    )
    fun outstandingBalances(): List<OutstandingBalanceProjection>

    interface OutstandingBalanceProjection {
        fun getStudentId(): Long
        fun getStudentName(): String
        fun getGradeLevel(): String
        fun getTotalOutstanding(): BigDecimal
    }
}