package com.jobsalrt.worker.schedulers.jobSarkari

import com.jobsalrt.worker.domain.BasicDetails
import com.jobsalrt.worker.domain.JobUrl
import com.jobsalrt.worker.domain.Post
import com.jobsalrt.worker.schedulers.PostFetcher
import com.jobsalrt.worker.webClient.WebClientWrapper
import org.jsoup.nodes.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class JobSarkariPostFetcher(@Autowired webClientWrapper: WebClientWrapper) : PostFetcher(webClientWrapper) {
    fun fetchPost(jobUrl: JobUrl): Mono<Post> {
        return fetch(jobUrl)
    }

    override fun getBasicDetails(document: Document): BasicDetails? {
        val name = document.select("h1").text()
        val map = mutableMapOf<String, String>()
        document.select("#post-title1").select("p")
            .toList()
            .forEach {
                val key = it.select(".option_name_l").text()
                    .split(":")[0]
                    .trim()
                    .toLowerCase()
                map[key] = it.select(".sec_detail").text().trim()
            }

        return BasicDetails(
            name,
            formTye = findValue(map, "form type"),
            totalVacancies = findValue(map, "total vacancies")?.toLong(),
            location = findValue(map, "location"),
            company = findValue(map, "company"),
            qualification = findValue(map, "qualification"),
//            lastDate = LocalDate.parse(findValue(map, "last date")),
        )
    }

    private fun findValue(map: Map<String, String>, key: String): String? {
        val keyName = map.keys.find {
            it.contains(Regex(key, RegexOption.IGNORE_CASE))
        }
        return map[keyName]
    }
}
