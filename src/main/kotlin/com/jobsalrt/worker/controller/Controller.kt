package com.jobsalrt.worker.controller

import com.jobsalrt.worker.controller.view.BasicDetailsView
import com.jobsalrt.worker.service.PostService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono


@RestController
class Controller(
    @Autowired val postService: PostService
) {
    @GetMapping("/awake")
    fun get(): Mono<String> {
        return Mono.empty()
    }

    @GetMapping("/posts/page/{page}")
    fun getAllPosts(@PathVariable page: String): Mono<List<BasicDetailsView>> {
        return postService.getAllPosts(page)
            .map { posts ->
                posts.map {
                    BasicDetailsView.from(it)
                }
            }
    }
}

