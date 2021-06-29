package com.jobsalrt.worker.service

import com.jobsalrt.worker.domain.RawPost
import com.jobsalrt.worker.service.postService.PostService
import com.jobsalrt.worker.service.postService.RawPostService
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class Notifier(
    val postService: PostService,
    val rawPostService: RawPostService,
    val communicationService: CommunicationService
) {
    fun notify(): Flux<RawPost> {
        return rawPostService.findAllUnNotified()
            .flatMap { rawPost ->
                postService.findBySource(rawPost.source)
                    .flatMap {
                        communicationService.notify(it)
                    }.flatMap {
                        rawPost.isNotified = true
                        rawPostService.save(rawPost)
                    }
                    .onErrorContinue { throwable, u ->
                        println(u)
                        throwable.printStackTrace()
                    }
            }
    }
}
