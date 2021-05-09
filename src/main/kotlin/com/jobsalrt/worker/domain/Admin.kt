package com.jobsalrt.worker.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate


const val ADMIN_COLLECTION = "admins"

@TypeAlias("Admin")
@Document(ADMIN_COLLECTION)
data class Admin(
    @Id
    var id: ObjectId? = null,
    val name: String,
    val email: String,
    val password: String,
    val createdAt: LocalDate = LocalDate.now(),
    var token: String? = null
)
