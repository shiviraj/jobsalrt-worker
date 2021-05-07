package com.jobsalrt.worker.repository

import com.jobsalrt.worker.domain.RawPost
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface RawPostRepository : ReactiveCrudRepository<RawPost, String> {
    fun findBySource(url: String): Mono<RawPost>
}
