package com.sncfi.payment_system.repository

import com.sncfi.payment_system.entity.SmsNotification
import org.springframework.data.jpa.repository.JpaRepository

interface SmsNotificationRepository : JpaRepository<SmsNotification, Long>