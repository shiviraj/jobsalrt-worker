package com.jobsalrt.worker.service

import com.jobsalrt.worker.domain.BasicDetails
import com.jobsalrt.worker.domain.Post
import com.jobsalrt.worker.webClient.WebClientWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class CommunicationService(@Autowired val webClientWrapper: WebClientWrapper) {
    fun notify(post: Post): Mono<String> {
        return webClientWrapper.post(
            baseUrl = "https://hooks.slack.com",
            path = "/services/T011LMY6ZF0/B020XFHELGK/N2iVXm7ONaQiugoNhPp7iJng",
            body = createMessageBody(post.basicDetails, post.failures),
            returnType = String::class.java,
        )
    }

    fun notify(source: String, old: String, new: String): Mono<String> {
        return webClientWrapper.post(
            baseUrl = "https://hooks.slack.com",
            path = "/services/T011LMY6ZF0/B025DEF0ABD/OabKfnu8KTNkIgFahR7tJ6T2",
            body = mapOf(
                "blocks" to listOf(
                    mapOf(
                        "type" to "section",
                        "fields" to listOf(
                            mapOf(
                                "type" to "mrkdwn",
                                "text" to old
                            ),
                            mapOf(
                                "type" to "mrkdwn",
                                "text" to new
                            ),
                        )
                    ),
                    mapOf(
                        "type" to "section",
                        "fields" to listOf(
                            mapOf(
                                "type" to "mrkdwn",
                                "text" to source
                            )
                        )
                    )

                )
            ),
            returnType = String::class.java,
        )
    }

    private fun createMessageBody(
        basicDetails: BasicDetails,
        failures: List<String>
    ): Map<String, List<Map<String, Any>>> {
        return mapOf(
            "blocks" to listOf(
                mapOf(
                    "type" to "header",
                    "text" to mapOf(
                        "type" to "plain_text",
                        "text" to basicDetails.name,
                        "emoji" to true
                    )
                ),
                mapOf(
                    "type" to "section",
                    "fields" to listOf(
                        mapOf(
                            "type" to "mrkdwn",
                            "text" to failures.joinToString("\n")
                        ),
                        mapOf(
                            "type" to "mrkdwn",
                            "text" to "<https://jobsalrt-admin.herokuapp.com/post/${basicDetails.url}|*Update Post*>"
                        ),
                    )
                )
            )
        )
    }
}
