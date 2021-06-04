package com.jobsalrt.worker.repository

import com.jobsalrt.worker.domain.JobUrl
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface JobUrlRepository : ReactiveCrudRepository<JobUrl, String> {
    @Query(value = "{status: \"TO_FETCH\"}")
    fun findAllNotFetched(pageable: PageRequest): Flux<JobUrl>

    fun findByUrl(url: String): Mono<JobUrl>
}
