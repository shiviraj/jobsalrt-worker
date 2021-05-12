package com.jobsalrt.worker.domain

import org.bson.types.ObjectId
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.annotation.TypeAlias
import org.springframework.data.mongodb.core.index.Indexed
import org.springframework.data.mongodb.core.mapping.Document
import java.time.LocalDate

const val POST_COLLECTION = "posts"

@TypeAlias("Post")
@Document(POST_COLLECTION)
data class Post(
    @Id
    var id: ObjectId? = null,
    var basicDetails: BasicDetails? = null,
    var states: List<State> = emptyList(),
    var dates: Details? = null,
    var feeDetails: Details? = null,
    var vacancyDetails: Details? = null,
    var ageLimit: Details? = null,
    var selectionProcess: List<String>? = null,
    var howToApply: List<String>? = null,
    var importantLinks: List<Link>? = null,
    var others: Map<String, Details>? = null,
    @Indexed(unique = true)
    val source: String,
    val createdAt: LocalDate = LocalDate.now(),
    val status: Status = Status.NOT_VERIFIED,
    var isUpdateAvailable: Boolean = false,
    val otherSource: String? = null,
    var failures: List<String> = emptyList(),
    var totalViews: Long = 0
) {
    @LastModifiedDate
    var postUpdateDate: LocalDate = LocalDate.now()
}

data class Link(
    val name: String,
    val link: List<Pair<String, String>>
)

data class BasicDetails(
    val name: String,
    var formType: FormType? = null,
    val advtNo: String? = null,
    val lastDate: LocalDate? = null,
    val totalVacancies: Long? = null,
    val location: String? = null,
    val company: String? = null,
    val qualification: String? = null,
    val ageLimit: Pair<LocalDate, LocalDate>? = null,
    val postLogo: String = "",
    var url: String? = null
)

data class State(
    val type: Type,
    val createdAt: LocalDate = LocalDate.now()
)

data class Details(
    val header: List<String> = emptyList(),
    val body: List<List<String>>
)

enum class FormType {
    ONLINE,
    OFFLINE;

    companion object {
        fun of(type: String): FormType {
            return if (type.toLowerCase().matches(Regex("online"))) ONLINE else OFFLINE
        }
    }
}

enum class Type {
    ADMIT_CARD,
    RESULT,
    SYLLABUS,
    ANSWER_KEY
}

enum class Status {
    NOT_VERIFIED,
    DISABLED,
    VERIFIED
}
