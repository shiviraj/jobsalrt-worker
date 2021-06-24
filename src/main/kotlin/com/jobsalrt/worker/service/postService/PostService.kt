package com.jobsalrt.worker.service.postService

import com.jobsalrt.worker.domain.Post
import com.jobsalrt.worker.domain.Status
import com.jobsalrt.worker.repository.PostRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class PostService(
    @Autowired private val postRepository: PostRepository,
) {

    fun save(post: Post): Mono<Post> {
        return postRepository.save(post)
    }

    fun findBySource(source: String): Mono<Post> {
        return postRepository.findBySource(source)
    }

    fun getPostByUrl(url: String): Mono<Post> {
        return postRepository.findByBasicDetailsUrl(url)
    }

    fun deletePostByUrl(url: String): Mono<Post> {
        return postRepository.deleteByBasicDetailsUrl(url)
    }

    fun replaceSource(oldSource: String, source: String): Mono<Post> {
        return findBySource(oldSource)
            .flatMap {
                it.source = source
                save(it)
            }
    }

    fun markedAsUpdateAvailable(source: String): Mono<Post> {
        return findBySource(source)
            .flatMap {
                if (it.status != Status.DISABLED) it.isUpdateAvailable = true
                save(it)
            }
    }
}
