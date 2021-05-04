package com.jobsalrt.worker.service

import com.jobsalrt.worker.repository.PostRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class PostService(@Autowired private val postRepository: PostRepository) {

}
