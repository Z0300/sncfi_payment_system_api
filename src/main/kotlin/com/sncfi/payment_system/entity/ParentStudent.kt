package com.sncfi.payment_system.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.LocalDateTime

@Entity
@Table(
    name = "parent_students",
    uniqueConstraints = [
        UniqueConstraint(name = "uq_parent_students_pair", columnNames = ["user_id", "student_id"])
    ]
)
class ParentStudent(

    // FetchType.LAZY on both sides — a parent-student link is only ever
    // loaded to check/list access, so eagerly pulling the full User or
    // Student would be wasted work most of the time.
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User,

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    var student: Student,

    @Column(name = "created_at", insertable = false, updatable = false)
    var createdAt: LocalDateTime? = null

) : BaseEntity()
