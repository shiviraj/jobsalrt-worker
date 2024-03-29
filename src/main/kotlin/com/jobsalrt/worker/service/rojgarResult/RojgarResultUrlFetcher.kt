package com.jobsalrt.worker.service.rojgarResult

import com.jobsalrt.worker.domain.JobUrl
import com.jobsalrt.worker.schedulers.UrlFetcher
import com.jobsalrt.worker.service.JobUrlService
import com.jobsalrt.worker.webClient.WebClientWrapper
import org.jsoup.nodes.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class RojgarResultUrlFetcher(
    @Autowired private val webClientWrapper: WebClientWrapper,
    @Autowired private val jobUrlService: JobUrlService
) : UrlFetcher(jobUrlService, webClientWrapper) {
    fun fetch(): Flux<JobUrl> {
        return fetch(baseUrl = "https://www.rojgarresult.com", path = "/")
    }

    override fun getJobUrls(document: Document): List<JobUrl> {
        return document.select("table ul")
            .select("a")
            .toList()
            .map { element ->
                JobUrl(name = element.text().trim(), url = element.attr("href").trim())
            }
    }
}
