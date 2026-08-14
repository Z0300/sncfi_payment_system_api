package com.sncfi.payment_system.repository

import com.sncfi.payment_system.entity.Payment
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface PaymentRepository : JpaRepository<Payment, Long> {
    fun findByStudentIdOrderByCreatedAtDesc(studentId: Long): List<Payment>
    fun findByStudentId(studentId: Long, pageable: Pageable): Page<Payment>
}