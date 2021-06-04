package com.jobsalrt.worker.service.postService

import com.jobsalrt.worker.builder.RawPostBuilder
import com.jobsalrt.worker.repository.RawPostRepository
import com.jobsalrt.worker.test_utils.assertNextWith
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class RawPostServiceTest {
    private val rawPostRepository = mockk<RawPostRepository>()
    private val rawPostService = RawPostService(rawPostRepository)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `should save the raw post`() {
        val rawPost = RawPostBuilder().build()
        every { rawPostRepository.save(any()) } returns Mono.just(rawPost)

        val result = rawPostService.save(rawPost)

        assertNextWith(result) {
            it shouldBe rawPost
            verify(exactly = 1) {
                rawPostRepository.save(rawPost)
            }
        }
    }

    @Test
    fun `should give raw post based on url`() {
        val rawPost = RawPostBuilder(source = "source").build()
        every { rawPostRepository.findBySource(any()) } returns Mono.just(rawPost)

        val result = rawPostService.findBySource("source")

        assertNextWith(result) {
            it shouldBe rawPost
            verify(exactly = 1) {
                rawPostRepository.findBySource("source")
            }
        }
    }

    @Test
    fun `should find all raw post which are not notified`() {
        val rawPost = RawPostBuilder().build()
        val rawPost1 = RawPostBuilder(source = "source", isNotified = true).build()
        every { rawPostRepository.findAllByIsNotified(any(), any()) } returns Flux.just(rawPost, rawPost1)

        val result = rawPostService.findAllUnNotified()

        assertNextWith(result, { it shouldBe rawPost }, { it shouldBe rawPost1 })
        verify(exactly = 1) {
            rawPostRepository.findAllByIsNotified(false, any())
        }
    }
}
