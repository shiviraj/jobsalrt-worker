package com.jobsalrt.worker.service

import com.jobsalrt.worker.domain.Post
import com.jobsalrt.worker.webClient.WebClientWrapper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.format.DateTimeFormatter

@Service
class CommunicationService(@Autowired val webClientWrapper: WebClientWrapper) {
    fun notify(post: Post): Mono<String> {
        return webClientWrapper.post(
            baseUrl = "https://hooks.slack.com",
            path = "/services/T011LMY6ZF0/B025DEF0ABD/OabKfnu8KTNkIgFahR7tJ6T2",
            body = createMessageBody(post),
            returnType = String::class.java,
        )
    }


    private fun createMessageBody(post: Post): Map<String, List<Map<String, Any>>> {
        return mapOf(
            "blocks" to listOf(
                mapOf(
                    "type" to "header",
                    "text" to mapOf(
                        "type" to "plain_text",
                        "text" to post.basicDetails.name,
                        "emoji" to true
                    ),
                ),
                mapOf(
                    "type" to "section",
                    "text" to mapOf(
                        "type" to "plain_text",
                        "text" to "Last Update on: ${post.postUpdateDate.format(DateTimeFormatter.ISO_DATE_TIME)}",
                        "emoji" to true
                    ),
                    "accessory" to mapOf(
                        "type" to "radio_buttons",
                        "action_id" to "this_is_an_action_id",
                        "initial_option" to mapOf(
                            "value" to "TODO",
                            "text" to mapOf(
                                "type" to "plain_text",
                                "text" to "TODO"
                            ),
                        ),
                        "options" to listOf(
                            mapOf(
                                "value" to "TODO",
                                "text" to mapOf(
                                    "type" to "plain_text",
                                    "text" to "TODO"
                                ),
                            ),
                            mapOf(
                                "value" to "DOING",
                                "text" to mapOf(
                                    "type" to "plain_text",
                                    "text" to "DOING"
                                ),
                            ),
                            mapOf(
                                "value" to "DONE",
                                "text" to mapOf(
                                    "type" to "plain_text",
                                    "text" to "DONE"
                                ),
                            ),
                        ),
                    ),
                ),
            ),
        )
    }
}
