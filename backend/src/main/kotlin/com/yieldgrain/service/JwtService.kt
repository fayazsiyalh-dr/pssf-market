package com.yieldgrain.service

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.util.Date
import javax.crypto.SecretKey

@Service
class JwtService(
    @Value("${jwt.secret}") private val secret: String,
    @Value("${jwt.expiration-minutes}") private val expirationMinutes: Long
) {
    private val key: SecretKey = Keys.hmacShaKeyFor(secret.toByteArray(Charsets.UTF_8))

    fun generateToken(username: String, roles: List<String>): String {
        val now = Date()
        val expiry = Date(now.time + expirationMinutes * 60L * 1000L)

        return Jwts.builder()
            .subject(username)
            .claim("roles", roles)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(key)
            .compact()
    }

    fun extractUsername(token: String): String? =
        parseClaims(token).subject

    fun isTokenValid(token: String, username: String): Boolean {
        val claims = parseClaims(token)
        return claims.subject == username && !claims.expiration.before(Date())
    }

    private fun parseClaims(token: String): Claims =
        Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .payload
}
