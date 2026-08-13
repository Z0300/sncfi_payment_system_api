package com.sncfi.payment_system.repository

import com.sncfi.payment_system.entity.ParentStudent
import org.springframework.data.jpa.repository.JpaRepository

interface ParentStudentRepository : JpaRepository<ParentStudent, Long> {
    fun existsByUserIdAndStudentId(userId: Long, studentId: Long): Boolean
    fun findByUserId(userId: Long): List<ParentStudent>
}
