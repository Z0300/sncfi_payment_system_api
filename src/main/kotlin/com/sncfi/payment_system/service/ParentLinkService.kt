package com.sncfi.payment_system.service

import com.sncfi.payment_system.entity.ParentStudent
import com.sncfi.payment_system.entity.Role
import com.sncfi.payment_system.exception.InvalidPaymentException
import com.sncfi.payment_system.exception.ResourceNotFoundException
import com.sncfi.payment_system.repository.ParentStudentRepository
import com.sncfi.payment_system.repository.StudentRepository
import com.sncfi.payment_system.repository.UserRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

@Service
class ParentLinkService(
    private val parentStudentRepository: ParentStudentRepository,
    private val userRepository: UserRepository,
    private val studentRepository: StudentRepository
) {
    @Transactional
    fun link(userId: Long, studentId: Long): ParentStudent {
        val user = userRepository.findById(userId)
            .orElseThrow { ResourceNotFoundException("User $userId not found") }
        if (user.role != Role.PARENT) {
            throw InvalidPaymentException("User $userId is not a parent account")
        }
        val student = studentRepository.findById(studentId)
            .orElseThrow { ResourceNotFoundException("Student $studentId not found") }
        if (parentStudentRepository.existsByUserIdAndStudentId(userId, studentId)) {
            throw InvalidPaymentException("Already linked")
        }
        return parentStudentRepository.save(ParentStudent(user = user, student = student))
    }
}