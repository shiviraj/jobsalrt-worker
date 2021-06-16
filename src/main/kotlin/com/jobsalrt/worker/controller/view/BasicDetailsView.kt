package com.jobsalrt.worker.controller.view

import com.jobsalrt.worker.domain.FormType
import com.jobsalrt.worker.domain.Post
import com.jobsalrt.worker.domain.Status
import org.bson.types.ObjectId
import java.time.LocalDate
import java.time.LocalDateTime


data class BasicDetailsView(
    val id: ObjectId? = null,
    val name: String,
    val formType: FormType? = FormType.ONLINE,
    val advtNo: String? = null,
    val lastDate: LocalDate? = null,
    val vacancies: Long? = null,
    val location: String? = null,
    val company: String? = null,
    val qualification: String? = null,
    val postLogo: String = "",
    val url: String,
    val createdAt: LocalDateTime,
    val postUpdateDate: LocalDateTime,
    val source: String,
    val status: Status,
    val isUpdateAvailable: Boolean,
    val totalViews: Long
) {
    companion object {
        fun from(post: Post): BasicDetailsView {
            val basicDetails = post.basicDetails
            return BasicDetailsView(
                id = post.id,
                name = basicDetails.name,
                formType = basicDetails.formType ?: FormType.ONLINE,
                advtNo = basicDetails.advtNo,
                lastDate = basicDetails.lastDate,
                vacancies = basicDetails.vacancies,
                location = basicDetails.location,
                company = basicDetails.company,
                qualification = basicDetails.qualification,
                postLogo = basicDetails.postLogo,
                url = basicDetails.url,
                createdAt = post.createdAt,
                postUpdateDate = post.postUpdateDate,
                isUpdateAvailable = post.isUpdateAvailable,
                source = post.source,
                status = post.status,
                totalViews = post.totalViews
            )
        }
    }
}
