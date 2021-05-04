package com.jobsalrt.worker.schedulers

import com.jobsalrt.worker.domain.JobUrl
import com.jobsalrt.worker.service.JobUrlService
import com.jobsalrt.worker.webClient.WebClientWrapper
import org.springframework.dao.DuplicateKeyException
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

abstract class UrlFetcher(
    private val jobUrlService: JobUrlService,
    private val webClientWrapper: WebClientWrapper
) {
    fun fetch(baseUrl: String, path: String = ""): Flux<JobUrl> {
        return webClientWrapper.get(
            baseUrl = baseUrl,
            path = path,
            returnType = String::class.java,
        )
            .flatMapMany {
                val jobUrls = getJobUrls(it)
                saveAll(jobUrls)
            }
    }

    abstract fun getJobUrls(htmlString: String): List<JobUrl>

    private fun saveAll(jobUrls: List<JobUrl>): Flux<JobUrl> {
        return Flux.fromIterable(jobUrls)
            .flatMapSequential { jobUrl ->
                jobUrlService.save(jobUrl)
                    .onErrorResume {
                        if (it is DuplicateKeyException)
                            Mono.empty()
                        else throw  it
                    }
            }
    }
}
