package com.sncfi.payment_system.dto

import com.sncfi.payment_system.entity.Student
import jakarta.validation.constraints.NotBlank

data class CreateStudentRequest(
    @field:NotBlank val name: String,
    @field:NotBlank val gradeLevel: String,
    val contactNumber: String? = null
)

data class StudentResponse(
    val id: Long,
    val name: String,
    val gradeLevel: String,
    val contactNumber: String?
)

fun Student.toResponse() = StudentResponse(
    id = id!!,
    name = name,
    gradeLevel = gradeLevel,
    contactNumber = contactNumber
)

data class LinkParentRequest(
    val userId: Long,
    val studentId: Long
)