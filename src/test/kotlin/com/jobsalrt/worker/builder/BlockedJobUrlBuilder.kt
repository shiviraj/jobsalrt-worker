package com.jobsalrt.worker.builder

import com.jobsalrt.worker.domain.BlockedJobUrl
import org.bson.types.ObjectId

data class BlockedJobUrlBuilder(
    val id: ObjectId? = null,
    val name: String = "",
    val url: String = ""
) {
    fun build(): BlockedJobUrl {
        return BlockedJobUrl(
            id = id,
            name = name,
            url = url
        )
    }
}
