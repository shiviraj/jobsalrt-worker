package com.jobsalrt.worker.service

import com.jobsalrt.worker.domain.JobUrl
import com.jobsalrt.worker.domain.JobUrlStatus
import com.jobsalrt.worker.repository.JobUrlRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class JobUrlService(@Autowired private val jobUrlRepository: JobUrlRepository) {
    fun save(jobUrl: JobUrl): Mono<JobUrl> {
        return jobUrlRepository.save(jobUrl)
    }

    fun getAllNotFetched(): Flux<JobUrl> {
        val pageable = PageRequest.of(1, 25)
        return jobUrlRepository.findAllNotFetched(pageable)
            .flatMap {
                it.retryCount += 1
                save(it)
            }
    }

    fun findByUrl(url: String): Mono<JobUrl> {
        return jobUrlRepository.findByUrl(url)
    }

    fun deleteAll(): Mono<Void> {
        return jobUrlRepository.deleteAll()
    }

    fun replaceJobUrl(oldUrl: String, newUrl: String): Mono<JobUrl> {
        return findByUrl(oldUrl)
            .flatMap {
                it.url = newUrl
                save(it)
            }
    }

    fun markedAsFailed(jobUrl: JobUrl): Mono<JobUrl> {
        if (jobUrl.retryCount > 3) {
            jobUrl.status = JobUrlStatus.FAILED
            return save(jobUrl)
        }
        return Mono.just(jobUrl)
    }
}
