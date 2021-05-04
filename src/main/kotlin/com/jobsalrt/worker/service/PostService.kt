package com.jobsalrt.worker.service

import com.jobsalrt.worker.domain.Post
import com.jobsalrt.worker.repository.PostRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class PostService(@Autowired private val postRepository: PostRepository) {
    fun save(post: Post): Mono<Post> {
        return postRepository.save(post)
    }
}
