package com.sncfi.payment_system.controller

import com.sncfi.payment_system.dto.ChargeResponse
import com.sncfi.payment_system.dto.CreateStudentRequest
import com.sncfi.payment_system.dto.LinkParentRequest
import com.sncfi.payment_system.dto.PageResponse
import com.sncfi.payment_system.dto.StatementResponse
import com.sncfi.payment_system.dto.StudentResponse
import com.sncfi.payment_system.dto.toPageResponse
import com.sncfi.payment_system.dto.toResponse
import com.sncfi.payment_system.security.SecurityUtils
import com.sncfi.payment_system.service.ChargeService
import com.sncfi.payment_system.service.ParentAccessService
import com.sncfi.payment_system.service.ParentLinkService
import com.sncfi.payment_system.service.StatementService
import com.sncfi.payment_system.service.StudentService
import jakarta.validation.Valid
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/students")
class StudentController(
    private val studentService: StudentService,
    private val chargeService: ChargeService,
    private val statementService: StatementService,
    private val parentLinkService: ParentLinkService,
    private val parentAccessService: ParentAccessService
) {

    @PreAuthorize("hasAnyRole('CASHIER', 'ADMIN')")
    @PostMapping
    fun create(@Valid @RequestBody request: CreateStudentRequest): StudentResponse =
        studentService.create(request).toResponse()

    @PreAuthorize("hasAnyRole('CASHIER', 'ADMIN')")
    @GetMapping
    fun search(
        @RequestParam(required = false, defaultValue = "") search: String,
        @PageableDefault(size = 20, sort = ["name"]) pageable: Pageable
    ): PageResponse<StudentResponse> =
        studentService.search(search, pageable).toPageResponse { it.toResponse() }

    @PreAuthorize(
        "hasAnyRole('CASHIER', 'ADMIN') or " +
                "@parentAccessService.isLinked(authentication.principal.userId, #id)"
    )
    @GetMapping("/{id}/charges")
    fun outstandingCharges(
        @PathVariable id: Long,
        @RequestParam(required = false, defaultValue = "false") all: Boolean
    ): List<ChargeResponse> {
        val charges = if (all) chargeService.allChargesFor(id) else chargeService.outstandingChargesFor(id)
        return charges.map { it.toResponse() }
    }

    @PreAuthorize(
        "hasAnyRole('CASHIER', 'ADMIN') or " +
                "@parentAccessService.isLinked(authentication.principal.userId, #id)"
    )
    @GetMapping("/{id}/statement")
    fun statement(@PathVariable id: Long): StatementResponse =
        statementService.getStatement(id)

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/parent-links")
    fun linkParent(@RequestBody request: LinkParentRequest): ResponseEntity<Void> {
        parentLinkService.link(request.userId, request.studentId)
        return ResponseEntity.status(HttpStatus.CREATED).build()
    }

    @PreAuthorize("hasRole('PARENT')")
    @GetMapping("/my-children")
    fun myChildren(): List<StudentResponse> =
        parentAccessService.linkedStudents(SecurityUtils.currentUserId()!!).map { it.toResponse() }
}