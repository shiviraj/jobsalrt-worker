package com.jobsalrt.worker.service.sarkariResult

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
import reactor.core.publisher.Mono

class SarkariResultUrlFetcherTest {
    private val jobUrlService = mockk<JobUrlService>()
    private val webClientWrapper = mockk<WebClientWrapper>()
    private val sarkariResultUrlFetcher = SarkariResultUrlFetcher(jobUrlService, webClientWrapper)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `should fetch all the urls`() {
        val jobUrl = JobUrlBuilder(name = "name", url = "http://sarkariresults.info/url").build()
        val jobUrl1 = JobUrlBuilder(name = "name1", url = "http://sarkariresults.info/url1").build()

        val htmlString = """<html>
                        <head></head>
                        <body>
                        <table><tr><td>
                            <ul><li><a href="http://sarkariresults.info/url">name</a></li></ul>
                            <ul><li><a href="http://sarkariresults.info/url1">name1</a></li></ul>
                        </td></tr></table>
                        </body>
                        </html>"""

        every {
            webClientWrapper.get(
                baseUrl = any(),
                path = any(),
                returnType = any<Class<*>>(),
            )
        } returns Mono.just(htmlString)
        every { jobUrlService.save(any()) } returns Mono.just(jobUrl) andThen Mono.just(jobUrl1)

        val fetch = sarkariResultUrlFetcher.fetch()
        assertNextWith(fetch, {
            it shouldBe jobUrl
        }, { it shouldBe jobUrl1 })
        verify(exactly = 1) {
            webClientWrapper.get(
                baseUrl = "https://sarkariresults.info",
                path = "/",
                returnType = String::class.java,
            )
            jobUrlService.save(jobUrl)
            jobUrlService.save(jobUrl1)
        }
    }

    @Test
    fun `should get the urls from the html document`() {
        val jobUrl = JobUrlBuilder(name = "name", url = "http://sarkariresults.info/url").build()
        val jobUrl1 = JobUrlBuilder(name = "name1", url = "http://sarkariresults.info/url1").build()

        val htmlString = """<html>
                        <head></head>
                        <body>
                        <table><tr><td>
                            <ul><li><a href="http://sarkariresults.info/url">name</a></li></ul>
                            <ul><li><a href="http://sarkariresults.info/url1">name1</a></li></ul>
                        </td></tr></table>
                        </body>
                        </html>"""
        val jobUrls = sarkariResultUrlFetcher.getJobUrls(Jsoup.parse(htmlString))
        assertSoftly {
            jobUrls shouldHaveSize 2
            jobUrls shouldBe listOf(jobUrl, jobUrl1)
        }
    }
}
