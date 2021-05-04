package com.jobsalrt.worker.schedulers

import com.jobsalrt.worker.domain.BasicDetails
import com.jobsalrt.worker.domain.JobUrl
import com.jobsalrt.worker.domain.Post
import com.jobsalrt.worker.webClient.WebClientWrapper
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import reactor.core.publisher.Mono

abstract class PostFetcher(private val webClientWrapper: WebClientWrapper) {
    fun fetch(jobUrl: JobUrl): Mono<Post> {
        return webClientWrapper.get(
            baseUrl = jobUrl.url,
            path = "",
            returnType = String::class.java,
        )
            .map {
                val post = Post(source = jobUrl.url)
                val document = Jsoup.parse(it)
                post.basicDetails = getBasicDetails(document)
                println(post)
                post
            }
    }


    abstract fun getBasicDetails(document: Document): BasicDetails?
}
