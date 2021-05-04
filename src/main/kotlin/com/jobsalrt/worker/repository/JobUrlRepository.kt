package com.jobsalrt.worker.repository

import com.jobsalrt.worker.domain.JobUrl
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface JobUrlRepository : ReactiveCrudRepository<JobUrl, String>
