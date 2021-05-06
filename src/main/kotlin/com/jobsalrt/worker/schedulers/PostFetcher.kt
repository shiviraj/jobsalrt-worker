package com.jobsalrt.worker.schedulers

import com.jobsalrt.worker.domain.*
import com.jobsalrt.worker.service.CommunicationService
import com.jobsalrt.worker.service.PostService
import com.jobsalrt.worker.webClient.WebClientWrapper
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.dao.DuplicateKeyException
import reactor.core.publisher.Mono

abstract class PostFetcher(
    private val webClientWrapper: WebClientWrapper,
    private val postService: PostService,
    private val communicationService: CommunicationService
) {
    fun fetch(jobUrl: JobUrl): Mono<Post> {
        return webClientWrapper.get(
            baseUrl = jobUrl.url,
            path = "",
            returnType = String::class.java,
        )
            .map {
                val post = Post(source = jobUrl.url)
                updatePost(post, it)
            }
            .flatMap {
                postService.save(it)
            }
            .onErrorResume {
                if (it is DuplicateKeyException)
                    Mono.empty()
                else throw it
            }
    }

    private fun updatePost(post: Post, htmlString: String): Post {
        val errorStacks = mutableListOf<String>()
        val document = Jsoup.parse(htmlString)
        updateDetails("Basic Details", errorStacks) { post.basicDetails = getBasicDetails(document) }
        updateDetails("Dates", errorStacks) { post.dates = getDates(document) }
        updateDetails("Fee details", errorStacks) { post.feeDetails = getFeeDetails(document) }
        updateDetails("Vacancy Details", errorStacks) { post.vacancyDetails = getVacancyDetails(document) }
        updateDetails("Age Limit Details", errorStacks) { post.ageLimit = getAgeLimitDetails(document) }
        updateDetails("Selection Process", errorStacks) { post.selectionProcess = getSelectionProcessDetails(document) }
        updateDetails("How to Apply", errorStacks) { post.howToApply = getHowToApplyDetails(document) }
        updateDetails("Important Links", errorStacks) { post.importantLinks = getImportantLinks(document) }
        updateDetails("Other Details", errorStacks) { post.others = getOtherDetails(document) }
        sendFailureNotification(errorStacks, post)
        return post
    }

    private fun sendFailureNotification(errorStacks: List<String>, post: Post) {
        if (errorStacks.isNotEmpty())
            communicationService.sendFailureAlert(errorStacks, post).subscribe()
    }

    private fun updateDetails(errorMessage: String, errorStacks: MutableList<String>, function: () -> Unit) {
        try {
            function()
        } catch (e: Exception) {
            errorStacks.add("Failed to update $errorMessage")
            println("Failed to update $errorMessage")
        }
    }

    abstract fun getOtherDetails(document: Document): Map<String, Details>?

    abstract fun getImportantLinks(document: Document): List<Link>?

    abstract fun getHowToApplyDetails(document: Document): List<String>?

    abstract fun getSelectionProcessDetails(document: Document): List<String>?

    abstract fun getAgeLimitDetails(document: Document): Details?

    abstract fun getVacancyDetails(document: Document): Details?

    abstract fun getFeeDetails(document: Document): Details?

    abstract fun getDates(document: Document): Details?

    abstract fun getBasicDetails(document: Document): BasicDetails?
}
