package com.sncfi.payment_system.entity

import com.sncfi.payment_system.entity.BaseEntity
import com.sncfi.payment_system.entity.Charge
import com.sncfi.payment_system.entity.InventoryItem
import com.sncfi.payment_system.entity.Payment
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.math.BigDecimal

enum class PaymentLineType { CHARGE, INVENTORY }

/**
 * NOTE: validation that (lineType == CHARGE => charge set, inventoryItem null)
 * and vice versa is intentionally NOT enforced in an `init {}` block here.
 * Hibernate hydrates entities via the kotlin-jpa plugin's synthetic no-arg
 * constructor, which runs init blocks with fields still null/default —
 * a require() check here would throw while simply *loading* a row from
 * the DB, not just when constructing a new one.
 *
 * The DB-level CHECK constraint (chk_payment_lines_type_matches_ref) is
 * the source of truth. Mirror that same validation in the service layer
 * BEFORE building a PaymentLine to insert, e.g. in a PaymentService
 * factory method — not inside the entity itself.
 */
@Entity
@Table(name = "payment_lines")
class PaymentLine(

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "payment_id", nullable = false)
    var payment: Payment,

    @Enumerated(EnumType.STRING)
    @Column(name = "line_type", nullable = false, columnDefinition = "ENUM('CHARGE','INVENTORY')")
    var lineType: PaymentLineType,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "charge_id")
    var charge: Charge? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_item_id")
    var inventoryItem: InventoryItem? = null,

    @Column(nullable = false)
    var quantity: Int = 1,

    @Column(nullable = false, precision = 10, scale = 2)
    var amount: BigDecimal

) : BaseEntity() {

    companion object {
        /** Use this instead of the raw constructor so the CHARGE/INVENTORY invariant can't be skipped. */
        fun forCharge(payment: Payment, charge: Charge, amount: BigDecimal) =
            PaymentLine(
                payment = payment,
                lineType = PaymentLineType.CHARGE,
                charge = charge,
                quantity = 1,
                amount = amount
            )

        /** Use this instead of the raw constructor so the CHARGE/INVENTORY invariant can't be skipped. */
        fun forInventoryItem(payment: Payment, item: InventoryItem, quantity: Int, amount: BigDecimal) =
            PaymentLine(
                payment = payment,
                lineType = PaymentLineType.INVENTORY,
                inventoryItem = item,
                quantity = quantity,
                amount = amount
            )
    }
}
