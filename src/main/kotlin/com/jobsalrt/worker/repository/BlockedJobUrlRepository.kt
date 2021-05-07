package com.jobsalrt.worker.repository

import com.jobsalrt.worker.domain.BlockedJobUrl
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface BlockedJobUrlRepository : ReactiveCrudRepository<BlockedJobUrl, String>
