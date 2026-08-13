package com.sncfi.payment_system.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "students")
class Student(

    @Column(nullable = false, length = 255)
    var name: String,

    @Column(name = "grade_level", nullable = false, length = 50)
    var gradeLevel: String,

    @Column(name = "contact_number", length = 20)
    var contactNumber: String? = null,

    @Column(name = "created_at", insertable = false, updatable = false)
    var createdAt: LocalDateTime? = null,

    @Column(name = "updated_at", insertable = false, updatable = false)
    var updatedAt: LocalDateTime? = null

) : BaseEntity()
