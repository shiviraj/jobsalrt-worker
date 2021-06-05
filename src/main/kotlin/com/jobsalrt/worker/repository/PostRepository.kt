package com.jobsalrt.worker.repository

import com.jobsalrt.worker.domain.Post
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface PostRepository : ReactiveCrudRepository<Post, String> {
    fun findBySource(source: String): Mono<Post>
    fun deleteByBasicDetailsUrl(url: String): Mono<Post>
    fun findByBasicDetailsUrl(url: String): Mono<Post>
}
