package com.jobsalrt.worker.service

import com.jobsalrt.worker.domain.Post
import com.jobsalrt.worker.webClient.WebClientWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CommunicationService(@Autowired val webClientWrapper: WebClientWrapper) {
    fun sendFailureAlert(errorStacks: List<String>, post: Post): Mono<String> {
        return webClientWrapper.post(
            baseUrl = "https://hooks.slack.com",
            path = "/services/T011LMY6ZF0/B020XFHELGK/N2iVXm7ONaQiugoNhPp7iJng",
            body = createMessageBody(post, errorStacks),
            returnType = String::class.java,
        )
    }

    private fun createMessageBody(
        post: Post,
        errorStacks: List<String>
    ) = mapOf(
        "blocks" to listOf(
            mapOf(
                "type" to "header",
                "text" to mapOf(
                    "type" to "plain_text",
                    "text" to "Failed ${post.source}",
                    "emoji" to true
                )
            ),
            mapOf(
                "type" to "section",
                "fields" to listOf(
                    mapOf(
                        "type" to "mrkdwn",
                        "text" to errorStacks.joinToString("\n")
                    ),
                )
            )
        )
    )
}
