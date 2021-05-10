package com.jobsalrt.worker.controller.view

import com.jobsalrt.worker.domain.FormType
import com.jobsalrt.worker.domain.Post
import java.time.LocalDate


data class BasicDetailsView(
    val name: String,
    val formTye: FormType,
    val advtNo: String? = null,
    val lastDate: LocalDate? = null,
    val totalVacancies: Long? = null,
    val location: String? = null,
    val company: String? = null,
    val qualification: String? = null,
    val ageLimit: Pair<LocalDate, LocalDate>? = null,
    val postLogo: String = "",
    val url: String,
    val createdAt: LocalDate,
    val postUpdateDate: LocalDate,
    val source: String,
    val isVerified: Boolean,
    val isUpdateAvailable: Boolean,
    val totalViews: Long
) {
    companion object {
        fun from(post: Post): BasicDetailsView {
            val basicDetails = post.basicDetails
            return BasicDetailsView(
                name = basicDetails?.name ?: "",
                formTye = basicDetails?.formTye ?: FormType.ONLINE,
                advtNo = basicDetails?.advtNo,
                lastDate = basicDetails?.lastDate,
                totalVacancies = basicDetails?.totalVacancies,
                location = basicDetails?.location,
                company = basicDetails?.company,
                qualification = basicDetails?.qualification,
                ageLimit = basicDetails?.ageLimit,
                postLogo = basicDetails?.postLogo ?: "",
                url = basicDetails?.url ?: "",
                createdAt = post.createdAt,
                postUpdateDate = post.postUpdateDate,
                source = post.source,
                isVerified = post.isVerified,
                isUpdateAvailable = post.isUpdateAvailable,
                totalViews = post.totalViews
            )
        }
    }
}
