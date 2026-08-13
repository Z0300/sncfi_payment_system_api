package com.sncfi.payment_system.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime

enum class FeeCategory { TUITION, FEE, OTHER }

@Entity
@Table(name = "fee_items")
class FeeItem(

    @Column(nullable = false, length = 255)
    var name: String,

    @Column(name = "default_amount", nullable = false, precision = 10, scale = 2)
    var defaultAmount: BigDecimal,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('TUITION','FEE','OTHER')")
    var category: FeeCategory,

    @Column(name = "created_at", insertable = false, updatable = false)
    var createdAt: LocalDateTime? = null,

    @Column(name = "updated_at", insertable = false, updatable = false)
    var updatedAt: LocalDateTime? = null

) : BaseEntity()
