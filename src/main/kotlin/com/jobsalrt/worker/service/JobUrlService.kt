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
        if (jobUrl.url.isEmpty()) return Mono.error(Throwable("Empty Url"))
        return jobUrlRepository.save(jobUrl)
    }

    fun getAllNotFetched(): Flux<JobUrl> {
        val pageable = PageRequest.of(0, 100)
        return jobUrlRepository.findByStatus(JobUrlStatus.TO_FETCH, pageable)
            .flatMap {
                it.retryCount += 1
                if (it.retryCount > 3) it.status = JobUrlStatus.FAILED
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
                if (it.retryCount > 3) it.status = JobUrlStatus.FAILED
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

    fun saveAll(jobUrls: List<JobUrl>): Flux<JobUrl> {
        return jobUrlRepository.findAll()
            .map { it.url }
            .collectList()
            .flatMapMany { urls ->
                val unsavedUrls = jobUrls
                    .distinctBy { it.url }
                    .filterNot {
                        urls.contains(it.url) || it.url.isEmpty()
                    }
                jobUrlRepository.saveAll(unsavedUrls)
            }
    }
}
