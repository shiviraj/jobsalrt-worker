package com.jobsalrt.worker.schedulers

import com.jobsalrt.worker.domain.*
import com.jobsalrt.worker.service.CommunicationService
import com.jobsalrt.worker.service.PostService
import com.jobsalrt.worker.service.RawPostService
import com.jobsalrt.worker.webClient.WebClientWrapper
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono

@Service
abstract class PostFetcher(
    private val webClientWrapper: WebClientWrapper,
    private val postService: PostService,
    private val communicationService: CommunicationService,
    private val rawPostService: RawPostService
) {
    fun fetch(jobUrl: JobUrl): Mono<RawPost> {
        return webClientWrapper.get(
            baseUrl = jobUrl.url,
            path = "",
            returnType = String::class.java,
        )
            .map {
                Jsoup.parse(it)
            }
            .flatMap { document ->
                rawPostService.findBySource(jobUrl.url)
                    .flatMap {
                        updateRawPostIfAvailable(document, it)
                    }
                    .switchIfEmpty(
                        addPostAndRawPost(document, jobUrl)
                    )
            }.onErrorResume {
                Mono.empty()
            }
    }

    @Transactional(rollbackForClassName = ["Exception"])
    fun addPostAndRawPost(document: Document, jobUrl: JobUrl): Mono<RawPost> {
        return Mono.just(document)
            .flatMap {
                val post = createPost(Post(source = jobUrl.url), document)
                postService.save(post)
            }
            .flatMap {
                rawPostService.save(RawPost(html = parseHtml(document), source = jobUrl.url))
            }
    }

    @Transactional(rollbackForClassName = ["Exception"])
    fun updateRawPostIfAvailable(document: Document, rawPost: RawPost): Mono<RawPost> {
        val html = parseHtml(document)
        return if (rawPost.html != html) {
            postService.findBySource(rawPost.source)
                .flatMap {
                    it.isUpdateAvailable = true
                    postService.save(it)
                }
                .flatMap {
                    rawPost.html = html
                    rawPostService.save(rawPost)
                }
        } else {
            Mono.just(rawPost)
        }
    }

    abstract fun parseHtml(document: Document): String

    private fun createPost(post: Post, document: Document): Post {
        try {
            val errorStacks = mutableListOf<String>()
            updateDetails("Basic Details", errorStacks) { post.basicDetails = getBasicDetails(document) }
            updateDetails("Dates", errorStacks) { post.dates = getDates(document) }
            updateDetails("Fee details", errorStacks) { post.feeDetails = getFeeDetails(document) }
            updateDetails("Vacancy Details", errorStacks) { post.vacancyDetails = getVacancyDetails(document) }
            updateDetails("Age Limit Details", errorStacks) { post.ageLimit = getAgeLimitDetails(document) }
            updateDetails("Selection Process", errorStacks) {
                post.selectionProcess = getSelectionProcessDetails(document)
            }
            updateDetails("How to Apply", errorStacks) { post.howToApply = getHowToApplyDetails(document) }
            updateDetails("Important Links", errorStacks) { post.importantLinks = getImportantLinks(document) }
            updateDetails("Other Details", errorStacks) { post.others = getOtherDetails(document) }
            sendFailureNotification(errorStacks, post)
            post.failures = errorStacks
        } catch (e: Exception) {
        }
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
            errorStacks.add(errorMessage)
            println("Failed to update $errorMessage")
        }
    }

    abstract fun getOtherDetails(document: Document): Map<String, Details>?

    abstract fun getImportantLinks(document: Document): Details?

    abstract fun getHowToApplyDetails(document: Document): List<String>?

    abstract fun getSelectionProcessDetails(document: Document): List<String>?

    abstract fun getAgeLimitDetails(document: Document): Details?

    abstract fun getVacancyDetails(document: Document): Details?

    abstract fun getFeeDetails(document: Document): Details?

    abstract fun getDates(document: Document): Details?

    abstract fun getBasicDetails(document: Document): BasicDetails?

    fun parseImportantLinks(rows: List<Element>): Details {
        val body = mutableListOf<List<String>>()

        rows.map {
            val list = it.select("td").toList()
            val name = list.first().text().trim()
            val anchorTags = list[1].select("a").toList()
            if (anchorTags.size > 1) {
                body += anchorTags.map { anchorTag ->
                    listOf("$name (${anchorTag.text().trim()})", anchorTag.attr("href").trim())
                }
            } else {
                body.add(listOf(name, anchorTags[0].attr("href").trim()))
            }
        }
        return Details(body = body)
    }
}
