package com.jobsalrt.worker.service.jobSarkari

import com.jobsalrt.worker.builder.JobUrlBuilder
import com.jobsalrt.worker.service.JobUrlService
import com.jobsalrt.worker.test_utils.assertNextWith
import com.jobsalrt.worker.webClient.WebClientWrapper
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.jsoup.Jsoup
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.dao.DuplicateKeyException
import reactor.core.publisher.Mono

class JobSarkariUrlFetcherTest {
    private val jobUrlService = mockk<JobUrlService>()
    private val webClientWrapper = mockk<WebClientWrapper>()
    private val jobSarkariUrlFetcher = JobSarkariUrlFetcher(jobUrlService, webClientWrapper)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `should fetch all the urls from the job sarkari`() {
        val jobUrl = JobUrlBuilder(name = "name", url = "https://www.jobsarkari.com/url").build()
        val jobUrl1 = JobUrlBuilder(name = "name1", url = "https://www.jobsarkari.com/url1").build()

        val htmlString = """<div class="main_cards_container">
                <p><a href="https://www.jobsarkari.com/url">name</a></p>
                <p><a href="https://www.jobsarkari.com/url1">name1</a></p>
                </div>
                """

        every {
            webClientWrapper.get(
                baseUrl = any(),
                path = any(),
                returnType = any<Class<*>>()
            )
        } returns Mono.just(htmlString)
        every { jobUrlService.save(jobUrl) } returns Mono.just(jobUrl)
        every { jobUrlService.save(jobUrl1) } returns Mono.just(jobUrl1)

        val result = jobSarkariUrlFetcher.fetch()

        assertNextWith(result, {
            it shouldBe jobUrl
        }, {
            it shouldBe jobUrl1
        })

        verify(exactly = 1) {
            webClientWrapper.get(
                baseUrl = "https://www.jobsarkari.com",
                path = "/",
                returnType = String::class.java
            )
            jobUrlService.save(jobUrl)
            jobUrlService.save(jobUrl1)
        }

    }

    @Test
    fun `should not save all the urls if url is duplicate`() {
        val jobUrl = JobUrlBuilder(name = "name", url = "https://www.jobsarkari.com/url").build()

        val htmlString = """<div class="main_cards_container">
                <p><a href="https://www.jobsarkari.com/url">name</a></p>
                <p><a href="https://www.jobsarkari.com/url">name</a></p>
                </div>
                """

        every {
            webClientWrapper.get(
                baseUrl = any(),
                path = any(),
                returnType = any<Class<*>>()
            )
        } returns Mono.just(htmlString)
        every { jobUrlService.save(any()) } returns Mono.just(jobUrl) andThen Mono.error(DuplicateKeyException("Duplicate key"))

        val result = jobSarkariUrlFetcher.fetch()

        assertNextWith(result) {
            it shouldBe jobUrl
            verify(exactly = 1) {
                jobUrlService.save(any())
            }
        }
    }

    @Test
    fun `should get job Urls`() {
        val jobUrl = JobUrlBuilder(name = "name", url = "https://www.jobsarkari.com/url").build()

        val htmlString = """<div class="main_cards_container">
                <p><a href="https://www.jobsarkari.com/url">name</a></p>
                <p><a href="https://www.jobsarkari.com/url">name</a></p>
                </div>
                """

        val result = jobSarkariUrlFetcher.getJobUrls(Jsoup.parse(htmlString))
        assertSoftly {
            result shouldHaveSize 2
            result shouldBe listOf(jobUrl, jobUrl)
        }
    }
}
