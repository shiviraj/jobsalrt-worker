package com.jobsalrt.worker.service

import com.jobsalrt.worker.domain.JobUrl
import com.jobsalrt.worker.webClient.WebClientWrapper
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import reactor.core.publisher.Flux

abstract class UrlFetcher(
    private val jobUrlService: JobUrlService,
    private val webClientWrapper: WebClientWrapper
) {
    fun fetch(baseUrl: String, path: String): Flux<JobUrl> {
        return webClientWrapper.get(
            baseUrl = baseUrl,
            path = path,
            returnType = String::class.java,
        )
            .flatMapMany {
                val document = Jsoup.parse(it)
                val jobUrls = getJobUrls(document)
                jobUrlService.saveAll(jobUrls)
            }
    }

    abstract fun getJobUrls(document: Document): List<JobUrl>
}
