package com.jobsalrt.worker.builder

import com.jobsalrt.worker.domain.JobUrl
import org.bson.types.ObjectId

data class JobUrlBuilder(
    val id: ObjectId? = null,
    val name: String = "name",
    val url: String = "url",
    val isFetched: Boolean = false
) {
    fun build(): JobUrl {
        return JobUrl(
            id = id,
            name = name,
            url = url,
            isFetched = isFetched
        )
    }
}
