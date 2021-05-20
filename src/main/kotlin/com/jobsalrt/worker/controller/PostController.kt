package com.jobsalrt.worker.controller

import com.jobsalrt.worker.domain.Post
import com.jobsalrt.worker.service.PostService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/post")
class PostController(
    @Autowired val postService: PostService
) {

    @GetMapping("/{url}")
    fun getPostByUrl(@PathVariable url: String): Mono<Post> {
        return postService.getPostByUrl(url)
    }

    @PutMapping("/{url}")
    fun updatePost(@PathVariable url: String, @RequestBody post: Post): Mono<Post> {
        return postService.updatePost(url, post)
    }

    @GetMapping("/{url}/available")
    fun urlAvailable(@PathVariable url: String): Mono<Pair<Boolean, String>> {
        return postService.urlAvailable(url)
    }
}

