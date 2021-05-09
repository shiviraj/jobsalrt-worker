package com.jobsalrt.worker.repository

import com.jobsalrt.worker.domain.Admin
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface AdminRepository : ReactiveCrudRepository<Admin, String> {
    fun findByEmail(email: String): Mono<Admin>
    fun findByToken(token: String): Mono<Admin>
}
