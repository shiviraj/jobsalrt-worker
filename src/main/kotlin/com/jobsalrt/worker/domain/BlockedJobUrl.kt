package com.jobsalrt.worker.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

const val BLOCKED_JOB_URL_COLLECTION = "blockedJobUrls"

@TypeAlias("BlockedJobUrl")
@Document(BLOCKED_JOB_URL_COLLECTION)
data class BlockedJobUrl(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val name: String,
    @Indexed(unique = true)
    val url: String,
)
