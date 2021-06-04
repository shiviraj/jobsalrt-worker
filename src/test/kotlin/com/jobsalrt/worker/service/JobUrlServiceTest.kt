package com.jobsalrt.worker.service

import com.jobsalrt.worker.builder.JobUrlBuilder
import com.jobsalrt.worker.repository.JobUrlRepository
import com.jobsalrt.worker.test_utils.assertNextWith
import io.kotest.assertions.assertSoftly
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

class JobUrlServiceTest {
    private val jobUrlRepository = mockk<JobUrlRepository>()
    private val jobUrlService = JobUrlService(jobUrlRepository)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `should save url`() {
        val url = JobUrlBuilder(name = "jobUrl", url = "jobUrl").build()

        every { jobUrlRepository.save(any()) } returns Mono.just(url)

        val result = jobUrlService.save(url)

        assertNextWith(result) {
            it shouldBe url
            verify(exactly = 1) {
                jobUrlRepository.save(url)
            }
        }
    }

    @Test
    fun `should get all urls which are not fetched`() {
        val url = JobUrlBuilder().build()
        every { jobUrlRepository.findAllNotFetched(any()) } returns Flux.just(url)

        val urls = jobUrlService.getAllNotFetched()

        assertNextWith(urls) {
            it shouldBe url
        }
    }

    @Test
    fun `should find job url by url`() {
        val url = JobUrlBuilder().build()

        every { jobUrlRepository.findByUrl("url") } returns Mono.just(url)
        val jobUrl = jobUrlService.findByUrl("url")

        assertNextWith(jobUrl) {
            it shouldBe url
        }
    }

    @Test
    fun `should delete all job urls`() {
        every { jobUrlRepository.deleteAll() } returns Mono.empty()
        jobUrlService.deleteAll().block()

        assertSoftly {
            verify(exactly = 1) {
                jobUrlRepository.deleteAll()
            }
        }
    }
}
