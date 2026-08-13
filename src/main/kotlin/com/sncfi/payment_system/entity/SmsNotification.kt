package com.sncfi.payment_system.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDateTime

enum class SmsType { PAYMENT_CONFIRMATION, DUE_REMINDER, BALANCE_UPDATE }
enum class SmsStatus { SENT, FAILED }

@Entity
@Table(name = "sms_notifications")
class SmsNotification(

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    var student: Student,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "payment_id")
    var payment: Payment? = null,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('PAYMENT_CONFIRMATION','DUE_REMINDER','BALANCE_UPDATE')")
    var type: SmsType,

    @Column(nullable = false, columnDefinition = "TEXT")
    var message: String,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, columnDefinition = "ENUM('SENT','FAILED')")
    var status: SmsStatus,

    @Column(name = "sent_at", insertable = false, updatable = false)
    var sentAt: LocalDateTime? = null

) : BaseEntity()
