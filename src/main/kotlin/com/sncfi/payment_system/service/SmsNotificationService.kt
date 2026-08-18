package com.sncfi.payment_system.service

import com.sncfi.payment_system.entity.Payment
import com.sncfi.payment_system.entity.SmsNotification
import com.sncfi.payment_system.entity.SmsStatus
import com.sncfi.payment_system.entity.SmsType
import com.sncfi.payment_system.repository.SmsNotificationRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Thin wrapper behind a single interface so the actual provider
 * (Semaphore, Twilio, etc.) is swappable without touching PaymentService.
 * Every attempt is logged to sms_notifications, sent or failed.
 */
@Service
class SmsNotificationService(
    private val smsNotificationRepository: SmsNotificationRepository,
    private val smsSender: SmsSender
) {
    private val log = LoggerFactory.getLogger(javaClass)

    fun sendPaymentConfirmation(payment: Payment) {
        val student = payment.student
        val phone = student.contactNumber

        if (phone.isNullOrBlank()) {
            log.warn("Skipping payment-confirmation SMS for student ${student.id}: no contact number on file")
            return
        }

        val message = "Payment of ${payment.total} received for ${student.name}. Thank you."
        val status = try {
            smsSender.send(phone, message)
            SmsStatus.SENT
        } catch (ex: Exception) {
            log.error("SMS send failed for payment ${payment.id}", ex)
            SmsStatus.FAILED
        }

        smsNotificationRepository.save(
            SmsNotification(
                student = student,
                payment = payment,
                type = SmsType.PAYMENT_CONFIRMATION,
                message = message,
                status = status
            )
        )
    }
}

/** Swap the implementation for whichever provider you pick (Semaphore, Twilio, etc.). */
interface SmsSender {
    fun send(phoneNumber: String, message: String)
}