package com.sncfi.payment_system.repository

import com.sncfi.payment_system.entity.Student
import org.springframework.data.jpa.repository.JpaRepository

interface StudentRepository : JpaRepository<Student, Long> {
    fun findByNameContainingIgnoreCase(name: String): List<Student>
}