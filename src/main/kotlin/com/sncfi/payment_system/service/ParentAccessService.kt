package com.sncfi.payment_system.service

import com.sncfi.payment_system.entity.Student
import com.sncfi.payment_system.repository.ParentStudentRepository
import org.springframework.stereotype.Service

@Service
class ParentAccessService(
    private val parentStudentRepository: ParentStudentRepository
) {
    fun isLinked(parentUserId: Long, studentId: Long): Boolean =
        parentStudentRepository.existsByUserIdAndStudentId(parentUserId, studentId)

    fun linkedStudentIds(parentUserId: Long): List<Long> =
        parentStudentRepository.findByUserId(parentUserId).map { it.student.id!! }

    fun linkedStudents(parentUserId: Long): List<Student> =
        parentStudentRepository.findByUserId(parentUserId).map { it.student }
}