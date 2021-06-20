package com.jobsalrt.worker.service.postService

import com.jobsalrt.worker.domain.*
import com.jobsalrt.worker.service.JobUrlService
import com.jobsalrt.worker.webClient.RedirectionError
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
    private val rawPostService: RawPostService,
    private val jobUrlService: JobUrlService
) {
    fun fetch(jobUrl: JobUrl): Mono<RawPost> {
        return fetchPostFromUrl(jobUrl.url)
            .flatMap { document ->
                updateRawPostIfAvailable(document, jobUrl)
                    .switchIfEmpty(
                        addPostAndRawPost(document, jobUrl)
                    )
            }
            .doOnError {
                if (it is RedirectionError) {
                    val url = it.clientResponse.headers().header("location")[0]
                    jobUrlService.replaceJobUrl(jobUrl.url, url)
                        .flatMap {
                            postService.replaceSource(jobUrl.url, url)
                        }
                        .subscribe()
                }
            }
            .onErrorContinue { throwable, u ->
                println(u)
                throwable.printStackTrace()
                jobUrlService.markedAsFailed(jobUrl).subscribe()
            }
    }

    fun fetchPost(source: String): Mono<Post> {
        return fetchPostFromUrl(source)
            .map {
                createPost(Post(source = source), it)
            }
    }

    @Transactional(rollbackForClassName = ["Exception"])
    fun updateRawPostIfAvailable(document: Document, jobUrl: JobUrl): Mono<RawPost> {
        val html = parseHTMLText(document)

        return rawPostService.findBySource(jobUrl.url)
            .flatMap { rawPost ->
                if (rawPost.html != html) {
                    postService.markedAsUpdateAvailable(jobUrl.url)
                        .flatMap {
                            rawPostService.updateHtml(jobUrl.url, html)
                        }
                } else {
                    Mono.just(rawPost)
                }
            }
    }

    @Transactional(rollbackForClassName = ["Exception"])
    fun addPostAndRawPost(document: Document, jobUrl: JobUrl): Mono<RawPost> {
        return Mono.just(document)
            .flatMap {
                val post = createPost(Post(source = jobUrl.url), document)
                Mono.zip(
                    postService.save(post),
                    rawPostService.save(RawPost(html = parseHTMLText(document), source = jobUrl.url))
                )
                    .onErrorContinue { throwable, u ->
                        println(u)
                        throwable.printStackTrace()
                        postService.deletePostByUrl(jobUrl.url).subscribe()
                        rawPostService.deleteBySource(jobUrl.url).subscribe()
                    }
            }
            .map {
                it.t2
            }
    }

    private fun parseHTMLText(document: Document): String {
        return parseHtml(document)
            .replace(Regex("<[^a][^>]*>", RegexOption.IGNORE_CASE), "")
            .replace(Regex("<a.*href=\"([^h])[^>]*>", RegexOption.IGNORE_CASE), "")
            .replace(Regex("[ \n\t]"), "")
    }

    private fun fetchPostFromUrl(url: String): Mono<Document> {
        return webClientWrapper.get(
            baseUrl = url,
            path = "",
            returnType = String::class.java,
        )
            .map {
                Jsoup.parse(it)
            }
    }

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
            post.failures = errorStacks
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return post
    }

    private fun updateDetails(errorMessage: String, errorStacks: MutableList<String>, function: () -> Unit) {
        try {
            function()
        } catch (e: Exception) {
            errorStacks.add(errorMessage)
            println("Failed to update $errorMessage")
        }
    }

    abstract fun parseHtml(document: Document): String

    abstract fun getOtherDetails(document: Document): Map<String, Details>?

    abstract fun getImportantLinks(document: Document): Details?

    abstract fun getHowToApplyDetails(document: Document): List<String>?

    abstract fun getSelectionProcessDetails(document: Document): List<String>?

    abstract fun getAgeLimitDetails(document: Document): Details?

    abstract fun getVacancyDetails(document: Document): Details?

    abstract fun getFeeDetails(document: Document): Details?

    abstract fun getDates(document: Document): Details?

    abstract fun getBasicDetails(document: Document): BasicDetails

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
