package com.sncfi.payment_system.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.math.BigDecimal
import java.time.LocalDateTime

/**
 * Optional module — books/uniforms/supplies for walk-up sales.
 * Safe to leave unused (empty table) if this stays out of scope;
 * kept here since PaymentLine already models both line types.
 */
@Entity
@Table(name = "inventory_items")
class InventoryItem(

    @Column(nullable = false, length = 255)
    var name: String,

    @Column(nullable = false, precision = 10, scale = 2)
    var price: BigDecimal,

    @Column(length = 100, unique = true)
    var sku: String? = null,

    @Column(name = "stock_quantity", nullable = false)
    var stockQuantity: Int = 0,

    @Column(name = "created_at", insertable = false, updatable = false)
    var createdAt: LocalDateTime? = null,

    @Column(name = "updated_at", insertable = false, updatable = false)
    var updatedAt: LocalDateTime? = null

) : BaseEntity()
