package com.jobsalrt.worker.service

import com.jobsalrt.worker.controller.view.FilterRequest
import com.jobsalrt.worker.domain.Post
import com.jobsalrt.worker.repository.PostRepository
import com.jobsalrt.worker.repository.PostRepositoryOps
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.LocalDate

@Service
class PostService(
    @Autowired private val postRepository: PostRepository,
    @Autowired private val postRepositoryOps: PostRepositoryOps
) {

    fun save(post: Post): Mono<Post> {
        return postRepository.save(post)
    }

    fun findBySource(source: String): Mono<Post> {
        return postRepository.findBySource(source)
    }

    fun getAllPosts(page: Int, filterRequest: FilterRequest): Mono<List<Post>> {
        return postRepositoryOps.findPosts(filterRequest, page).collectList()
    }

    fun getPostsPageCount(filterRequest: FilterRequest): Mono<Pair<Long, Double>> {
        return postRepositoryOps.findPostCount(filterRequest)
    }

    fun getPostByUrl(url: String): Mono<Post> {
        return postRepositoryOps.findByBasicDetailsUrl(url)

    }

    fun updatePost(url: String, post: Post): Mono<Post> {
        return postRepositoryOps.findByBasicDetailsUrl(url)
            .flatMap {
                post.id = it.id
                post.postUpdateDate = LocalDate.now()
                save(post)
            }
    }

    fun urlAvailable(url: String): Mono<Pair<Boolean, String>> {
        return postRepositoryOps.findByBasicDetailsUrl(url)
            .map {
                Pair(false, it.source)
            }
            .switchIfEmpty(
                Mono.just(Pair(true, ""))
            )
    }

    fun addPost(post: Post): Mono<Post> {
        return postRepository.save(post)
    }
}
