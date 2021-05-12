package com.jobsalrt.worker.repository

import com.jobsalrt.worker.domain.Post
import org.springframework.data.domain.PageRequest
import org.springframework.data.mongodb.repository.Query
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Repository
interface PostRepository : ReactiveCrudRepository<Post, String> {
    fun findBySource(source: String): Mono<Post>

    @Query(value = "{}")
    fun findAllByQuery(of: PageRequest): Flux<Post>

    @Query(value = "{}")
    fun findAllByPagination(query: String, pageRequest: PageRequest): Flux<Post>
}
