package com.sncfi.payment_system.service

import com.sncfi.payment_system.dto.CreateStudentRequest
import com.sncfi.payment_system.entity.Student
import com.sncfi.payment_system.exception.ResourceNotFoundException
import com.sncfi.payment_system.repository.StudentRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service

@Service
class StudentService(
    private val studentRepository: StudentRepository
) {

    fun getById(id: Long): Student =
        studentRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("Student $id not found") }

    fun search(name: String, pageable: Pageable): Page<Student> =
        studentRepository.findByNameContainingIgnoreCase(name, pageable)

    fun create(request: CreateStudentRequest): Student =
        studentRepository.save(
            Student(
                name = request.name,
                gradeLevel = request.gradeLevel,
                contactNumber = request.contactNumber
            )
        )
}