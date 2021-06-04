package com.jobsalrt.worker.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document

const val JOB_URL_COLLECTION = "jobUrls"

@TypeAlias("JobUrl")
@Document(JOB_URL_COLLECTION)
data class JobUrl(
    @Id
    var id: ObjectId? = null,
    @Indexed(unique = true)
    val name: String,
    @Indexed(unique = true)
    var url: String,
    var status: JobUrlStatus = JobUrlStatus.TO_FETCH,
    var retryCount: Int = 0
)

enum class JobUrlStatus {
    TO_FETCH,
    FETCHING,
    FETCHED,
    FAILED
}
