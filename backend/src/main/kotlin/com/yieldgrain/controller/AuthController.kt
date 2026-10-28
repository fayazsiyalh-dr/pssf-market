package com.yieldgrain.controller

import com.yieldgrain.service.JwtService
import jakarta.validation.Valid
import jakarta.validation.constraints.NotBlank
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.ConcurrentHashMap

data class OtpRequest(
    @field:NotBlank val phoneNumber: String
)

data class OtpVerifyRequest(
    @field:NotBlank val phoneNumber: String,
    @field:NotBlank val otpCode: String
)

data class AuthResponse(
    val token: String,
    val phoneNumber: String,
    val roles: List<String>
)

@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val jwtService: JwtService,
    private val passwordEncoder: PasswordEncoder
) {
    private val otpStore = ConcurrentHashMap<String, String>()

    @PostMapping("/request-otp")
    fun requestOtp(@Valid @RequestBody request: OtpRequest): Map<String, String> {
        val normalizedPhone = normalizePhone(request.phoneNumber)
        val generatedOtp = (100000..999999).random().toString()
        otpStore[normalizedPhone] = generatedOtp
        return mapOf(
            "message" to "OTP sent successfully",
            "phoneNumber" to normalizedPhone,
            "debugOtp" to generatedOtp
        )
    }

    @PostMapping("/verify-otp")
    fun verifyOtp(@Valid @RequestBody request: OtpVerifyRequest): AuthResponse {
        val normalizedPhone = normalizePhone(request.phoneNumber)
        val storedOtp = otpStore[normalizedPhone]
        if (storedOtp == null || storedOtp != request.otpCode) {
            throw IllegalArgumentException("Invalid or expired OTP")
        }
        otpStore.remove(normalizedPhone)

        val roles = listOf("ROLE_CUSTOMER")
        val token = jwtService.generateToken(normalizedPhone, roles)
        return AuthResponse(token, normalizedPhone, roles)
    }

    @GetMapping("/health")
    fun health(): Map<String, String> = mapOf("status" to "UP")

    private fun normalizePhone(value: String): String = value.trim().replace(" ", "")
}
