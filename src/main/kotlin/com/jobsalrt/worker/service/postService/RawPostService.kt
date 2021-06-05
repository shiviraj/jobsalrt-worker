package com.jobsalrt.worker.service.postService

import com.jobsalrt.worker.domain.RawPost
import com.jobsalrt.worker.repository.RawPostRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Service
class RawPostService(@Autowired private val rawPostRepository: RawPostRepository) {
    fun save(post: RawPost): Mono<RawPost> {
        return rawPostRepository.save(post)
    }

    fun findBySource(url: String): Mono<RawPost> {
        return rawPostRepository.findBySource(url)
    }

    fun findAllUnNotified(): Flux<RawPost> {
        val pageable = PageRequest.of(0, 100)
        return rawPostRepository.findAllByIsNotified(false, pageable)
    }

    fun updateHtml(source: String, html: String): Mono<RawPost> {
        return findBySource(source)
            .flatMap {
                it.html = html
                save(it)
            }
    }

    fun deleteBySource(source: String): Mono<RawPost> {
        return rawPostRepository.deleteBySource(source)
    }
}
