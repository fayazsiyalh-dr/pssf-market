package com.yieldgrain.service

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.math.BigDecimal

class YieldGrainPricingServiceTest {

    private val service = YieldGrainPricingService()

    @Test
    fun `procurement fee is calculated at Rs 25 per maund`() {
        val fee = service.calculateProcurementFee(BigDecimal("120"))
        assertEquals(BigDecimal("3000"), fee)
    }

    @Test
    fun `storage fee is calculated at Rs 20 per maund per month`() {
        val fee = service.calculateStorageFee(BigDecimal("80"), 3)
        assertEquals(BigDecimal("4800"), fee)
    }

    @Test
    fun `resale brokerage is 05 percent of actual sale value`() {
        val brokerage = service.calculateResaleBrokerage(BigDecimal("200000"))
        assertEquals(BigDecimal("1000.00"), brokerage)
    }

    @Test
    fun `combined breakdown includes procurement storage and resale`() {
        val breakdown = service.calculateBreakdown(BigDecimal("50"), 2, BigDecimal("200000"))
        assertEquals(BigDecimal("1250"), breakdown.procurementFee)
        assertEquals(BigDecimal("2000"), breakdown.storageFee)
        assertEquals(BigDecimal("1000.00"), breakdown.resaleBrokerage)
    }
}
