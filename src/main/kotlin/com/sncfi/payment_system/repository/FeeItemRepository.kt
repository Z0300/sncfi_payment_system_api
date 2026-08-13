package com.sncfi.payment_system.repository

import com.sncfi.payment_system.entity.FeeItem
import org.springframework.data.jpa.repository.JpaRepository

interface FeeItemRepository : JpaRepository<FeeItem, Long> 