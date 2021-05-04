package com.jobsalrt.worker.schedulers.sarkariResult

import com.jobsalrt.worker.domain.JobUrl
import com.jobsalrt.worker.schedulers.UrlFetcher
import com.jobsalrt.worker.service.JobUrlService
import com.jobsalrt.worker.webClient.WebClientWrapper
import org.jsoup.Jsoup
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class SarkariResultUrlFetcher(
    @Autowired jobUrlService: JobUrlService,
    @Autowired webClientWrapper: WebClientWrapper
) :
    UrlFetcher(jobUrlService, webClientWrapper) {

    fun fetch(): Flux<JobUrl> {
        return fetch(baseUrl = "https://sarkariresults.info", path = "/")
    }

    override fun getJobUrls(htmlString: String): List<JobUrl> {
        val document = Jsoup.parse(htmlString)

        return document.select("table")[2]
            .select("a")
            .filter {
                !it.text().contains(Regex("view more", RegexOption.IGNORE_CASE))
            }
            .toList()
            .map { element ->
                JobUrl(name = element.text(), url = element.attr("href"))
            }
    }
}
