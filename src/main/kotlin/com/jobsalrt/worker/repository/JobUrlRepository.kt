package com.jobsalrt.worker.repository

import com.jobsalrt.worker.domain.JobUrl
import com.jobsalrt.worker.domain.JobUrlStatus
import org.springframework.data.domain.PageRequest
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface JobUrlRepository : ReactiveCrudRepository<JobUrl, String> {
    fun findByUrl(url: String): Mono<JobUrl>
    fun findByStatus(status: JobUrlStatus, pageable: PageRequest): Flux<JobUrl>
}
