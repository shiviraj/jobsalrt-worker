package com.jobsalrt.worker.repository

import com.jobsalrt.worker.domain.Post
import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface PostRepository : ReactiveCrudRepository<Post, String>
