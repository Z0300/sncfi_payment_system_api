package com.sncfi.payment_system.dto

import com.sncfi.payment_system.entity.FeeCategory
import java.math.BigDecimal
import java.time.LocalDate

data class CollectionsReportRequest(
    val fromDate: LocalDate,
    val toDate: LocalDate,
    val gradeLevel: String? = null
)

data class CollectionsByCategoryItem(
    val category: FeeCategory,
    val totalCollected: BigDecimal
)

data class CollectionsReportResponse(
    val fromDate: LocalDate,
    val toDate: LocalDate,
    val totalCollected: BigDecimal,
    val byCategory: List<CollectionsByCategoryItem>
)

data class OutstandingBalanceItem(
    val studentId: Long,
    val studentName: String,
    val gradeLevel: String,
    val totalOutstanding: BigDecimal
)