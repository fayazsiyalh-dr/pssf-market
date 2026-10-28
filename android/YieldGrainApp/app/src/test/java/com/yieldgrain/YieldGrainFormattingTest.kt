package com.yieldgrain

import org.junit.Assert.assertEquals
import org.junit.Test

class YieldGrainFormattingTest {
    @Test
    fun `pakistani rupees format should use PKR currency code`() {
        val formatted = formatPkrs(1250.0)
        assertEquals("PKR 1,250.00", formatted)
    }
}
