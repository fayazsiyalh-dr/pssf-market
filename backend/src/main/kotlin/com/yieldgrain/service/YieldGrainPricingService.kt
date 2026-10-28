package com.yieldgrain.service

import java.math.BigDecimal

data class FeeBreakdown(
    val procurementFee: BigDecimal,
    val storageFee: BigDecimal,
    val resaleBrokerage: BigDecimal,
    val totalEstimatedCharges: BigDecimal,
    val disclosedAdditionalCharges: BigDecimal = BigDecimal.ZERO
)

class YieldGrainPricingService {
    fun calculateProcurementFee(maunds: BigDecimal): BigDecimal = maunds.multiply(BigDecimal("25"))

    fun calculateStorageFee(maunds: BigDecimal, months: Int): BigDecimal =
        maunds.multiply(BigDecimal("20")).multiply(months.toBigDecimal())

    fun calculateResaleBrokerage(actualSaleValue: BigDecimal): BigDecimal =
        actualSaleValue.multiply(BigDecimal("0.005"))

    fun calculateBreakdown(maunds: BigDecimal, months: Int, actualSaleValue: BigDecimal): FeeBreakdown {
        val procurementFee = calculateProcurementFee(maunds)
        val storageFee = calculateStorageFee(maunds, months)
        val resaleBrokerage = calculateResaleBrokerage(actualSaleValue)

        return FeeBreakdown(
            procurementFee = procurementFee,
            storageFee = storageFee,
            resaleBrokerage = resaleBrokerage,
            totalEstimatedCharges = procurementFee.add(storageFee).add(resaleBrokerage),
            disclosedAdditionalCharges = BigDecimal.ZERO
        )
    }
}
