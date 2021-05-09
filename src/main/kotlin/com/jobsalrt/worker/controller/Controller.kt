package com.jobsalrt.worker.controller

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono


@RestController
class Controller {
    @GetMapping("/awake")
    fun get(): Mono<String> {
        return Mono.empty()
    }
}

