package com.jobsalrt.worker.service

import com.jobsalrt.worker.domain.BlockedJobUrl
import com.jobsalrt.worker.repository.BlockedJobUrlRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class BlockedJobUrlService(@Autowired private val blockedJobUrlRepository: BlockedJobUrlRepository) {
    fun getAll(): Flux<BlockedJobUrl> {
        return blockedJobUrlRepository.findAll()
    }
}
