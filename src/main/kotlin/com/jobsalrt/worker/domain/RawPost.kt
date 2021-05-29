package com.jobsalrt.worker.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

const val RAW_POST_COLLECTIONS = "rawPosts"

@TypeAlias("RawPost")
@Document(RAW_POST_COLLECTIONS)
data class RawPost(
    @Id
    var id: ObjectId? = null,
    var html: String? = null,
    @Indexed(unique = true)
    val source: String,
    val createdAt: LocalDate = LocalDate.now(),
    var isNotified: Boolean = false
)
