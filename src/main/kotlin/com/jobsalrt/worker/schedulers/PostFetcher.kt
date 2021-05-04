package com.jobsalrt.worker.schedulers

import com.jobsalrt.worker.domain.BasicDetails
import com.jobsalrt.worker.domain.Details
import com.jobsalrt.worker.domain.JobUrl
import com.jobsalrt.worker.domain.Post
import com.jobsalrt.worker.service.PostService
import com.jobsalrt.worker.webClient.WebClientWrapper
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.springframework.dao.DuplicateKeyException
import reactor.core.publisher.Mono

abstract class PostFetcher(private val webClientWrapper: WebClientWrapper, private val postService: PostService) {
    fun fetch(jobUrl: JobUrl): Mono<Post> {
        return webClientWrapper.get(
            baseUrl = jobUrl.url,
            path = "",
            returnType = String::class.java,
        )
            .map {
                println(jobUrl.url)
                val post = Post(source = jobUrl.url)
                val document = Jsoup.parse(it)
                post.basicDetails = getBasicDetails(document)
                post.dates = getDates(document)
                post.feeDetails = getFeeDetails(document)
                post.vacancyDetails = getVacancyDetails(document)
                post.ageLimit = getAgeLimitDetails(document)
                post.selectionProcess = getSelectionProcessDetails(document)
                post.howToApply = getHowToApplyDetails(document)
                post.importantLinks = getImportantLinks(document)
                post.others = getOtherDetails(document)
                post
            }
            .flatMap {
                postService.save(it)
            }
            .onErrorResume {
                if (it is DuplicateKeyException)
                    Mono.empty()
                else throw  it
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
}
