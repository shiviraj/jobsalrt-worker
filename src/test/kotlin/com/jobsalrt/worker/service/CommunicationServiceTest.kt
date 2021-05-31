package com.jobsalrt.worker.service

import com.jobsalrt.worker.builder.PostBuilder
import com.jobsalrt.worker.domain.Post
import com.jobsalrt.worker.test_utils.assertNextWith
import com.jobsalrt.worker.webClient.WebClientWrapper
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono

class CommunicationServiceTest {
    private val webClientWrapper = mockk<WebClientWrapper>()
    private val communicationService = CommunicationService(webClientWrapper)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `should send notification to user for a particular post update`() {
        every {
            webClientWrapper.post(
                baseUrl = any(),
                path = any(),
                body = any(),
                returnType = any<Class<*>>()
            )
        } returns Mono.just("ok")

        val post: Post = PostBuilder().build()
        val notify = communicationService.notify(post)

        val body = mapOf(
            "blocks" to listOf(
                mapOf(
                    "type" to "header",
                    "text" to mapOf(
                        "type" to "plain_text",
                        "text" to post.basicDetails.name,
                        "emoji" to true
                    )
                ),
                mapOf(
                    "type" to "section",
                    "fields" to listOf(
                        mapOf(
                            "type" to "mrkdwn",
                            "text" to post.failures.joinToString("\n")
                        ),
                        mapOf(
                            "type" to "mrkdwn",
                            "text" to "<https://jobsalrt-admin.herokuapp.com/post/${post.basicDetails.url}|*Update Post*>"
                        ),
                    )
                )
            )
        )

        assertNextWith(notify) {
            it shouldBe "ok"
            verify(exactly = 1) {
                webClientWrapper.post(
                    baseUrl = "https://hooks.slack.com",
                    path = "/services/T011LMY6ZF0/B020XFHELGK/N2iVXm7ONaQiugoNhPp7iJng",
                    body = body,
                    returnType = String::class.java,
                )
            }
        }
    }
}
