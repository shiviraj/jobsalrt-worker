package com.jobsalrt.worker.security

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.jobsalrt.worker.security.crypto.Crypto
import com.jobsalrt.worker.service.AdminService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.ZoneOffset

@Service
class WebToken(
    @Autowired val crypto: Crypto,
    @Autowired val adminService: AdminService
) {
    fun generateToken(email: String): String? {
        val issuedAt = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        val expiredAt = LocalDateTime.now().plusHours(1).toEpochSecond(ZoneOffset.UTC)
        return crypto.encrypt(AdminToken(email, issuedAt, expiredAt).toString())
    }

    fun extractEmail(token: String): String {
        return extractAdmin(token)?.email ?: ""
    }

    fun validateToken(token: String, userDetails: UserDetails): Boolean {
        val admin = extractAdmin(token) ?: return false
        val adminByToken = adminService.getAdminByToken(token).block()
        return admin.email == userDetails.username && isNotExpired(admin) && admin.email == adminByToken?.email
    }

    private fun isNotExpired(adminToken: AdminToken): Boolean {
        val now = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        return now > adminToken.issuedAt && now < adminToken.expiredAt

    }

    private fun extractAdmin(token: String): AdminToken? {
        val decrypt = crypto.decrypt(token)
        return AdminToken.from(decrypt)
    }
}

data class AdminToken(
    val email: String,
    val issuedAt: Long,
    val expiredAt: Long
) {
    override fun toString(): String {
        return ObjectMapperCache.objectMapper.writeValueAsString(this)
    }

    companion object {
        fun from(admin: String?): AdminToken? {
            return try {
                ObjectMapperCache.objectMapper.readValue(admin, AdminToken::class.java)
            } catch (e: Exception) {
                null
            }
        }
    }
}


object ObjectMapperCache {
    val objectMapper = jacksonObjectMapper()
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
}



