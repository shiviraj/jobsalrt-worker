package com.jobsalrt.worker.service.postService

import com.jobsalrt.worker.builder.BasicDetailsBuilder
import com.jobsalrt.worker.builder.PostBuilder
import com.jobsalrt.worker.service.jobSarkari.JobSarkariPostFetcher
import com.jobsalrt.worker.service.rojgarResult.RojgarResultPostFetcher
import com.jobsalrt.worker.service.sarkariResult.SarkariResultPostFetcher
import com.jobsalrt.worker.test_utils.assertNextWith
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class UpdateFetcherTest {

    private val jobSarkariPostFetcher = mockk<JobSarkariPostFetcher>()
    private val rojgarResultPostFetcher = mockk<RojgarResultPostFetcher>()
    private val sarkariResultPostFetcher = mockk<SarkariResultPostFetcher>()
    private val postService = mockk<PostService>()

    private val updateFetcher = UpdateFetcher(
        jobSarkariPostFetcher = jobSarkariPostFetcher,
        rojgarResultPostFetcher = rojgarResultPostFetcher,
        sarkariResultPostFetcher = sarkariResultPostFetcher,
        postService = postService
    )

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `should fetch the updates for job sarkari`() {
        val url = "http://jobsarkari.com/url"
        val post = PostBuilder(source = url, basicDetails = BasicDetailsBuilder(url = "url").build()).build()

        every { postService.getPostByUrl(any()) } returns Mono.just(post)
        every { jobSarkariPostFetcher.fetchPost(any()) } returns Mono.just(post)

        val result = updateFetcher.fetchUpdate("url")
        assertNextWith(result) {
            it shouldBe post
            verify(exactly = 1) {
                postService.getPostByUrl("url")
                jobSarkariPostFetcher.fetchPost(url)
            }
        }
    }

    @Test
    fun `should fetch the updates for sarkari result`() {
        val url = "http://sarkariresults.info/url"
        val post = PostBuilder(source = url, basicDetails = BasicDetailsBuilder(url = "url").build()).build()

        every { postService.getPostByUrl(any()) } returns Mono.just(post)
        every { sarkariResultPostFetcher.fetchPost(any()) } returns Mono.just(post)

        val result = updateFetcher.fetchUpdate("url")
        assertNextWith(result) {
            it shouldBe post
            verify(exactly = 1) {
                postService.getPostByUrl("url")
                sarkariResultPostFetcher.fetchPost(url)
            }
        }
    }

    @Test
    fun `should fetch the updates for rojgar result`() {
        val url = "http://rojgarresult.com/url"
        val post = PostBuilder(source = url, basicDetails = BasicDetailsBuilder(url = "url").build()).build()

        every { postService.getPostByUrl(any()) } returns Mono.just(post)
        every { rojgarResultPostFetcher.fetchPost(any()) } returns Mono.just(post)

        val result = updateFetcher.fetchUpdate("url")
        assertNextWith(result) {
            it shouldBe post
            verify(exactly = 1) {
                postService.getPostByUrl("url")
                rojgarResultPostFetcher.fetchPost(url)
            }
        }
    }

    @Test
    fun `should not fetch the updates if source is not matching with any of the original source`() {
        val url = "http://url.com/url"
        val post = PostBuilder(source = url, basicDetails = BasicDetailsBuilder(url = "url").build()).build()

        every { postService.getPostByUrl(any()) } returns Mono.just(post)

        val result = updateFetcher.fetchUpdate("url")

        StepVerifier.create(result).verifyComplete()
        verify(exactly = 1) {
            postService.getPostByUrl("url")
        }
    }
}
