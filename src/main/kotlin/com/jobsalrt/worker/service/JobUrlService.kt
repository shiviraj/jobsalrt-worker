package com.jobsalrt.worker.service

import com.jobsalrt.worker.domain.JobUrl
import com.jobsalrt.worker.repository.JobUrlRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class JobUrlService(@Autowired private val jobUrlRepository: JobUrlRepository) {
    fun save(jobUrl: JobUrl): Mono<JobUrl> {
        return jobUrlRepository.save(jobUrl)

    }

    fun getAllNotFetched(): Flux<JobUrl> {
        return jobUrlRepository.findAllNotFetched()
    }

    fun findByUrl(url: String): Mono<JobUrl> {
        return jobUrlRepository.findByUrl(url)
    }

    fun deleteAll(): Mono<Void> {
        return jobUrlRepository.deleteAll()
    }
}
