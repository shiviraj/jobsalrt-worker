package com.jobsalrt.worker.controller

import com.jobsalrt.worker.controller.view.BasicDetailsView
import com.jobsalrt.worker.controller.view.FilterRequest
import com.jobsalrt.worker.controller.view.PageCountView
import com.jobsalrt.worker.service.PostService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono


@RestController
class Controller(
    @Autowired val postService: PostService
) {
    @GetMapping("/awake")
    fun get(): Mono<String> {
        return Mono.empty()
    }

    @PostMapping("/posts/page/{page}")
    fun getAllPosts(@PathVariable page: Int, @RequestBody filterRequest: FilterRequest): Mono<List<BasicDetailsView>> {
        return postService.getAllPosts(page, filterRequest)
            .map { posts ->
                posts.map {
                    BasicDetailsView.from(it)
                }
            }
    }

    @PostMapping("/posts/pageCount")
    fun getPostsPageCount(@RequestBody filterRequest: FilterRequest): Mono<PageCountView> {
        return postService.getPostsPageCount(filterRequest)
            .map {
                PageCountView(page = it.second.toLong(), totalPost = it.first)
            }
    }
}

