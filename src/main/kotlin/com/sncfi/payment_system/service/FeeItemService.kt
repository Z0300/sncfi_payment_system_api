package com.sncfi.payment_system.service

import com.sncfi.payment_system.dto.CreateFeeItemRequest
import com.sncfi.payment_system.entity.FeeItem
import com.sncfi.payment_system.exception.ResourceNotFoundException
import com.sncfi.payment_system.repository.FeeItemRepository
import org.springframework.stereotype.Service

@Service
class FeeItemService(
    private val feeItemRepository: FeeItemRepository
) {

    fun getAll(): List<FeeItem> = feeItemRepository.findAll()

    fun getById(id: Long): FeeItem =
        feeItemRepository.findById(id)
            .orElseThrow { ResourceNotFoundException("FeeItem $id not found") }

    fun create(request: CreateFeeItemRequest): FeeItem =
        feeItemRepository.save(
            FeeItem(
                name = request.name,
                defaultAmount = request.defaultAmount,
                category = request.category
            )
        )
}