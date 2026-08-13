package com.sncfi.payment_system.repository

import com.sncfi.payment_system.entity.FeeCategory
import com.sncfi.payment_system.entity.PaymentLine
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.math.BigDecimal
import java.time.LocalDateTime

interface PaymentLineRepository : JpaRepository<PaymentLine, Long> {

    @Query(
        """
        SELECT COALESCE(SUM(pl.amount), 0)
        FROM PaymentLine pl
        JOIN pl.payment p
        WHERE pl.lineType = com.sncfi.paymentsystem.entity.PaymentLineType.CHARGE
          AND p.createdAt BETWEEN :from AND :to
          AND (:gradeLevel IS NULL OR p.student.gradeLevel = :gradeLevel)
        """
    )
    fun sumChargeCollections(
        @Param("from") from: LocalDateTime,
        @Param("to") to: LocalDateTime,
        @Param("gradeLevel") gradeLevel: String?
    ): BigDecimal

    @Query(
        """
        SELECT pl.charge.feeItem.category AS category, COALESCE(SUM(pl.amount), 0) AS total
        FROM PaymentLine pl
        JOIN pl.payment p
        WHERE pl.lineType = com.sncfi.paymentsystem.entity.PaymentLineType.CHARGE
          AND p.createdAt BETWEEN :from AND :to
          AND (:gradeLevel IS NULL OR p.student.gradeLevel = :gradeLevel)
        GROUP BY pl.charge.feeItem.category
        """
    )
    fun sumChargeCollectionsByCategory(
        @Param("from") from: LocalDateTime,
        @Param("to") to: LocalDateTime,
        @Param("gradeLevel") gradeLevel: String?
    ): List<CategoryTotal>

    interface CategoryTotal {
        fun getCategory(): FeeCategory
        fun getTotal(): BigDecimal
    }
}