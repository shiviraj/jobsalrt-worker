package com.jobsalrt.worker.service.jobSarkari

import com.jobsalrt.worker.domain.JobUrl
import com.jobsalrt.worker.schedulers.UrlFetcher
import com.jobsalrt.worker.service.JobUrlService
import com.jobsalrt.worker.webClient.WebClientWrapper
import org.jsoup.nodes.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class JobSarkariUrlFetcher(
    @Autowired jobUrlService: JobUrlService,
    @Autowired webClientWrapper: WebClientWrapper
) :
    UrlFetcher(jobUrlService, webClientWrapper) {

    fun fetch(): Flux<JobUrl> {
        return fetch(baseUrl = "https://www.jobsarkari.com", path = "/")
    }

    override fun getJobUrls(document: Document): List<JobUrl> {
        return document.select(".main_cards_container")
            .select("a")
            .toList()
            .map { element ->
                JobUrl(name = element.text().trim(), url = element.attr("href").trim())
            }
    }
}
