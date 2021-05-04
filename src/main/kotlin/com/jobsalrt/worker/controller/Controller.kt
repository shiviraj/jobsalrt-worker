package com.jobsalrt.worker.controller

import com.jobsalrt.worker.schedulers.MainSchedulers
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
class Controller(
    @Autowired val mainSchedulers: MainSchedulers
) {
    @GetMapping("/")
    fun get(): Mono<String> {
        mainSchedulers.start()
        return Mono.empty()
    }
}

