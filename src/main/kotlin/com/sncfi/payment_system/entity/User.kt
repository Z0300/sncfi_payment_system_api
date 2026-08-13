package com.sncfi.payment_system.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.time.LocalDateTime

enum class Role { PARENT, CASHIER, ADMIN }

@Entity
@Table(name = "users")
class User(

    @Column(nullable = false, unique = true, length = 50)
    var username: String,

    @Column(nullable = false, unique = true, length = 255)
    var email: String,

    @Column(name = "password_hash", nullable = false, length = 255)
    var passwordHash: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('PARENT','CASHIER','ADMIN')")
    var role: Role,

    @Column(name = "contact_number", length = 20)
    var contactNumber: String? = null,

    // DB-managed timestamps (DEFAULT CURRENT_TIMESTAMP / ON UPDATE CURRENT_TIMESTAMP).
    // insertable/updatable = false means MySQL owns these values entirely;
    // Hibernate only ever reads them back, never writes them.
    @Column(name = "created_at", insertable = false, updatable = false)
    var createdAt: LocalDateTime? = null,

    @Column(name = "updated_at", insertable = false, updatable = false)
    var updatedAt: LocalDateTime? = null

) : BaseEntity()
