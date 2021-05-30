package com.jobsalrt.worker.builder

import com.jobsalrt.worker.domain.Admin
import org.bson.types.ObjectId
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import java.time.LocalDate

data class AdminBuilder(
    val id: ObjectId? = null,
    val name: String = "Shiviraj",
    val email: String = "shivi@raj.com",
    val createdAt: LocalDate = LocalDate.of(2021, 1, 1),
    val token: String? = "token"
) {
    private val bCryptPasswordEncoder = BCryptPasswordEncoder()
    val password: String = bCryptPasswordEncoder.encode("password")
    fun build(): Admin {
        return Admin(
            id = id,
            name = name,
            email = email,
            password = password,
            createdAt = createdAt,
            token = token
        )
    }
}
