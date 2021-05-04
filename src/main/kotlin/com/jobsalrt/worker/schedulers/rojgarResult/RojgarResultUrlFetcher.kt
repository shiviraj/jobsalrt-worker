package com.jobsalrt.worker.schedulers.rojgarResult

import com.jobsalrt.worker.domain.JobUrl
import com.jobsalrt.worker.schedulers.UrlFetcher
import com.jobsalrt.worker.service.JobUrlService
import com.jobsalrt.worker.webClient.WebClientWrapper
import org.jsoup.Jsoup
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

    override fun getJobUrls(htmlString: String): List<JobUrl> {
        val document = Jsoup.parse(htmlString)
        val list = document.select("table")
            .select("a")
            .filter {
                it.attr("href").startsWith("https://www.rojgarresult.com/") &&
                    !it.text().contains(Regex("(<br>|view more)", RegexOption.IGNORE_CASE))
            }
            .toList()

        return list.subList(8, list.size)
            .map { element ->
                JobUrl(name = element.text(), url = element.attr("href"))
            }
    }
}
