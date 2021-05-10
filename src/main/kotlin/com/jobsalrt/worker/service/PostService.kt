package com.jobsalrt.worker.service

import com.jobsalrt.worker.domain.FormType
import com.jobsalrt.worker.domain.Post
import com.jobsalrt.worker.repository.PostRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDate

@Service
class PostService(@Autowired private val postRepository: PostRepository) {
    fun save(post: Post): Mono<Post> {
        return postRepository.save(post)
    }

    fun findBySource(source: String): Mono<Post> {
        return postRepository.findBySource(source)
    }

    fun getAllPosts(page: String): Mono<List<Post>> {
        val start = (page.toInt() - 1) * 50
        return postRepository.findAll()
            .collectList()
            .map {
                it.subList(start, start + 50)
            }
    }
}
