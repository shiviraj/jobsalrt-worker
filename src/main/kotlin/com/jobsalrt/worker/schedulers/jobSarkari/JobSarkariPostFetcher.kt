package com.jobsalrt.worker.schedulers.jobSarkari

import com.jobsalrt.worker.domain.JobUrl
import com.jobsalrt.worker.schedulers.PostFetcher
import com.jobsalrt.worker.webClient.WebClientWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class JobSarkariPostFetcher(@Autowired webClientWrapper: WebClientWrapper) : PostFetcher(webClientWrapper) {
    fun fetchPost(jobUrl: JobUrl): Mono<String> {
        return fetch(jobUrl)
    }
}
