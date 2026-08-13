package com.sncfi.payment_system.entity

import com.sncfi.payment_system.entity.PaymentLine
import com.sncfi.payment_system.entity.User
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime

enum class PaymentMethod { CASH, CARD }

@Entity
@Table(name = "payments")
class Payment(

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    var student: Student,

    // Nullable — ON DELETE SET NULL in the schema, so a deleted staff
    // account doesn't wipe out historical payment records.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "processed_by_user_id")
    var processedByUser: User? = null,

    @Column(nullable = false, precision = 10, scale = 2)
    var total: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(name = "payment_method", nullable = false, columnDefinition = "ENUM('CASH','CARD')")
    var paymentMethod: PaymentMethod,

    @Column(name = "amount_tendered", precision = 10, scale = 2)
    var amountTendered: BigDecimal? = null,

    @Column(name = "change_due", precision = 10, scale = 2)
    var changeDue: BigDecimal? = null,

    @Column(name = "created_at", insertable = false, updatable = false)
    var createdAt: LocalDateTime? = null,

    // cascade = ALL + orphanRemoval: PaymentLines have no independent
    // lifecycle — saving/deleting a Payment should save/delete its lines
    // with it. Matches the schema's ON DELETE CASCADE on payment_lines.
    @OneToMany(mappedBy = "payment", cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
    var lines: MutableList<PaymentLine> = mutableListOf()

) : BaseEntity() {

    /** Keeps both sides of the association in sync when building a Payment in code. */
    fun addLine(line: PaymentLine) {
        lines.add(line)
        line.payment = this
    }
}
