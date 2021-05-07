package com.jobsalrt.worker.service

import com.jobsalrt.worker.domain.RawPost
import com.jobsalrt.worker.repository.RawPostRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RawPostService(@Autowired private val rawPostRepository: RawPostRepository) {
    fun save(post: RawPost): Mono<RawPost> {
        return rawPostRepository.save(post)
    }

    fun findBySource(url: String): Mono<RawPost> {
        return rawPostRepository.findBySource(url)
    }
}
