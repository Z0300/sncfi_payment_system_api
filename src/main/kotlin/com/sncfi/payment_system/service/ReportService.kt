package com.sncfi.payment_system.service

import com.sncfi.payment_system.dto.CollectionsByCategoryItem
import com.sncfi.payment_system.dto.CollectionsReportRequest
import com.sncfi.payment_system.dto.CollectionsReportResponse
import com.sncfi.payment_system.dto.OutstandingBalanceItem
import com.sncfi.payment_system.repository.ChargeRepository
import com.sncfi.payment_system.repository.PaymentLineRepository
import org.springframework.stereotype.Service
import java.time.LocalTime

@Service
class ReportService(
    private val paymentLineRepository: PaymentLineRepository,
    private val chargeRepository: ChargeRepository
) {

    fun collectionsReport(request: CollectionsReportRequest): CollectionsReportResponse {
        // Whole-day range: from 00:00:00 on fromDate to 23:59:59.999 on toDate.
        val from = request.fromDate.atStartOfDay()
        val to = request.toDate.atTime(LocalTime.MAX)

        val total = paymentLineRepository.sumChargeCollections(from, to, request.gradeLevel)
        val byCategory = paymentLineRepository
            .sumChargeCollectionsByCategory(from, to, request.gradeLevel)
            .map { CollectionsByCategoryItem(category = it.getCategory(), totalCollected = it.getTotal()) }

        return CollectionsReportResponse(
            fromDate = request.fromDate,
            toDate = request.toDate,
            totalCollected = total,
            byCategory = byCategory
        )
    }

    fun outstandingBalances(): List<OutstandingBalanceItem> =
        chargeRepository.outstandingBalances().map {
            OutstandingBalanceItem(
                studentId = it.getStudentId(),
                studentName = it.getStudentName(),
                gradeLevel = it.getGradeLevel(),
                totalOutstanding = it.getTotalOutstanding()
            )
        }
}