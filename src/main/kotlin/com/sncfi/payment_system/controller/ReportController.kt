package com.sncfi.payment_system.controller

import org.springframework.web.bind.annotation.*
import org.springframework.security.access.prepost.PreAuthorize
import com.sncfi.payment_system.dto.CollectionsReportRequest
import com.sncfi.payment_system.dto.CollectionsReportResponse
import com.sncfi.payment_system.dto.OutstandingBalanceItem
import com.sncfi.payment_system.service.ReportService
import java.time.LocalDate

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasRole('ADMIN')")
class ReportController(
    private val reportService: ReportService
) {

    @GetMapping("/collections")
    fun collections(
        @RequestParam fromDate: LocalDate,
        @RequestParam toDate: LocalDate,
        @RequestParam(required = false) gradeLevel: String?
    ): CollectionsReportResponse =
        reportService.collectionsReport(CollectionsReportRequest(fromDate, toDate, gradeLevel))

    @GetMapping("/outstanding-balances")
    fun outstandingBalances(): List<OutstandingBalanceItem> =
        reportService.outstandingBalances()
}