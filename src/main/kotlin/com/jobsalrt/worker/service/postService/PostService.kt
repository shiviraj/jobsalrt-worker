package com.jobsalrt.worker.service.postService

import com.jobsalrt.worker.domain.Post
import com.jobsalrt.worker.repository.PostRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDateTime

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

    fun updatePost(url: String, post: Post): Mono<Post> {
        return postRepository.findByBasicDetailsUrl(url)
            .flatMap {
                post.id = it.id
                post.postUpdateDate = LocalDateTime.now()
                save(post)
            }
    }

    fun urlAvailable(url: String): Mono<Pair<Boolean, String>> {
        return postRepository.findByBasicDetailsUrl(url)
            .map {
                Pair(false, it.source)
            }
            .switchIfEmpty(
                Mono.just(Pair(true, ""))
            )
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
                it.isUpdateAvailable = true
                save(it)
            }
    }
}
