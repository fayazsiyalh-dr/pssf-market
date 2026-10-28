package com.yieldgrain.controller

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.math.BigDecimal

data class WheatRateResponse(
    val commodity: String,
    val buyRatePerMaund: BigDecimal,
    val sellRatePerMaund: BigDecimal,
    val unit: String,
    val publishedAt: String
)

@RestController
@RequestMapping("/api")
class RateController {

    @GetMapping("/rates")
    @PreAuthorize("hasAnyRole('CUSTOMER', 'ADMIN')")
    fun rates(): List<WheatRateResponse> = listOf(
        WheatRateResponse(
            commodity = "Wheat (Sindh)",
            buyRatePerMaund = BigDecimal("3300"),
            sellRatePerMaund = BigDecimal("3450"),
            unit = "PKR/maund",
            publishedAt = "2026-10-28T10:00:00+05:00"
        ),
        WheatRateResponse(
            commodity = "Wheat (Punjab)",
            buyRatePerMaund = BigDecimal("3180"),
            sellRatePerMaund = BigDecimal("3325"),
            unit = "PKR/maund",
            publishedAt = "2026-10-28T10:00:00+05:00"
        )
    )
}
