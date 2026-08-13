package com.sncfi.payment_system.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

enum class ChargeStatus { UNPAID, PARTIAL, PAID }

@Entity
@Table(name = "charges")
class Charge(

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    var student: Student,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "fee_item_id", nullable = false)
    var feeItem: FeeItem,

    @Column(name = "amount_due", nullable = false, precision = 10, scale = 2)
    var amountDue: BigDecimal,

    @Column(name = "amount_paid", nullable = false, precision = 10, scale = 2)
    var amountPaid: BigDecimal = BigDecimal.ZERO,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('UNPAID','PARTIAL','PAID')")
    var status: ChargeStatus = ChargeStatus.UNPAID,

    @Column(name = "due_date")
    var dueDate: LocalDate? = null,

    @Column(name = "created_at", insertable = false, updatable = false)
    var createdAt: LocalDateTime? = null,

    @Column(name = "updated_at", insertable = false, updatable = false)
    var updatedAt: LocalDateTime? = null

) : BaseEntity() {

    /** Remaining balance owed on this charge. Convenience only — not persisted. */
    val remainingBalance: BigDecimal
        get() = amountDue.subtract(amountPaid)
}
