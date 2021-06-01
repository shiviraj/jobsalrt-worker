package com.jobsalrt.worker.builder

import com.jobsalrt.worker.domain.RawPost
import org.bson.types.ObjectId
import java.time.LocalDate

data class RawPostBuilder(
    val id: ObjectId? = null,
    val html: String = "html data",
    val source: String = "http://sarkariresult.com/job1",
    val createdAt: LocalDate = LocalDate.of(2021, 1, 1),
    val isNotified: Boolean = false
) {
    fun build(): RawPost {
        return RawPost(
            id = id,
            html = html,
            source = source,
            createdAt = createdAt,
            isNotified = isNotified
        )
    }
}
