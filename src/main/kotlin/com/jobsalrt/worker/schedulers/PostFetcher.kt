package com.jobsalrt.worker.schedulers

import com.jobsalrt.worker.domain.JobUrl
import com.jobsalrt.worker.webClient.WebClientWrapper
import org.jsoup.Jsoup
import reactor.core.publisher.Mono

open class PostFetcher(private val webClientWrapper: WebClientWrapper) {
    fun fetch(jobUrl: JobUrl): Mono<String> {
        return webClientWrapper.get(
            baseUrl = jobUrl.url,
            path = "",
            returnType = String::class.java,
        )
            .map {
                val document = Jsoup.parse(it)
                println(document)
                it
            }
    }
}
