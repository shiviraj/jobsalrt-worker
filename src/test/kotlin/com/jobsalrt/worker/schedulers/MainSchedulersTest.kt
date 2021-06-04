package com.jobsalrt.worker.schedulers

import com.jobsalrt.worker.builder.BlockedJobUrlBuilder
import com.jobsalrt.worker.builder.JobUrlBuilder
import com.jobsalrt.worker.builder.PostBuilder
import com.jobsalrt.worker.builder.RawPostBuilder
import com.jobsalrt.worker.domain.JobUrl
import com.jobsalrt.worker.domain.RawPost
import com.jobsalrt.worker.service.BlockedJobUrlService
import com.jobsalrt.worker.service.CommunicationService
import com.jobsalrt.worker.service.JobUrlService
import com.jobsalrt.worker.service.jobSarkari.JobSarkariPostFetcher
import com.jobsalrt.worker.service.jobSarkari.JobSarkariUrlFetcher
import com.jobsalrt.worker.service.postService.PostService
import com.jobsalrt.worker.service.postService.RawPostService
import com.jobsalrt.worker.service.rojgarResult.RojgarResultPostFetcher
import com.jobsalrt.worker.service.rojgarResult.RojgarResultUrlFetcher
import com.jobsalrt.worker.service.sarkariResult.SarkariResultPostFetcher
import com.jobsalrt.worker.service.sarkariResult.SarkariResultUrlFetcher
import com.jobsalrt.worker.utils.DateProvider
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.mockk.*
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

class MainSchedulersTest {
    private val jobUrlService = mockk<JobUrlService>()
    private val jobSarkariUrlFetcher = mockk<JobSarkariUrlFetcher>()
    private val rojgarResultUrlFetcher = mockk<RojgarResultUrlFetcher>()
    private val sarkariResultUrlFetcher = mockk<SarkariResultUrlFetcher>()
    private val jobSarkariPostFetcher = mockk<JobSarkariPostFetcher>()
    private val rojgarResultPostFetcher = mockk<RojgarResultPostFetcher>()
    private val sarkariResultPostFetcher = mockk<SarkariResultPostFetcher>()
    private val blockedJobUrlService = mockk<BlockedJobUrlService>()
    private val communicationService = mockk<CommunicationService>()
    private val rawPostService = mockk<RawPostService>()
    private val postService = mockk<PostService>()
    private val dateProvider = mockk<DateProvider>()

    private val mainSchedulers = MainSchedulers(
        jobUrlService = jobUrlService,
        jobSarkariUrlFetcher = jobSarkariUrlFetcher,
        rojgarResultUrlFetcher = rojgarResultUrlFetcher,
        sarkariResultUrlFetcher = sarkariResultUrlFetcher,
        jobSarkariPostFetcher = jobSarkariPostFetcher,
        rojgarResultPostFetcher = rojgarResultPostFetcher,
        sarkariResultPostFetcher = sarkariResultPostFetcher,
        blockedJobUrlService = blockedJobUrlService,
        communicationService = communicationService,
        rawPostService = rawPostService,
        postService = postService,
        dateProvider = dateProvider
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
    fun `should updated rawPost notified as true if it send notification on slack for newer update`() {
        val rawPost = RawPostBuilder(source = "source").build()
        val post = PostBuilder(source = "source").build()

        every { dateProvider.getHour() } returns 0
        every { rojgarResultUrlFetcher.fetch() } returns Flux.empty()
        every { jobSarkariUrlFetcher.fetch() } returns Flux.empty()
        every { sarkariResultUrlFetcher.fetch() } returns Flux.empty()
        every { blockedJobUrlService.getAll() } returns Flux.empty()
        every { jobUrlService.getAllNotFetched() } returns Flux.empty()
        every { rawPostService.findAllUnNotified() } returns Flux.just(rawPost)
        every { postService.findBySource("source") } returns Mono.just(post)
        every { communicationService.notify(any()) } returns Mono.just("ok")
        every { rawPostService.save(any()) } returns Mono.just(rawPost)


        mainSchedulers.start()

        val savedRawPost = slot<RawPost>()

        assertSoftly {
            verify(exactly = 1) {
                rawPostService.save(capture(savedRawPost))
                postService.findBySource("source")
                communicationService.notify(post)
            }
            savedRawPost.captured.isNotified shouldBe true
        }
    }

    @Test
    fun `should not updated rawPost notified as true if it fails to send notification on slack`() {
        val rawPost = RawPostBuilder(source = "source").build()
        val post = PostBuilder(source = "source").build()

        every { dateProvider.getHour() } returns 0
        every { rojgarResultUrlFetcher.fetch() } returns Flux.empty()
        every { jobSarkariUrlFetcher.fetch() } returns Flux.empty()
        every { sarkariResultUrlFetcher.fetch() } returns Flux.empty()
        every { blockedJobUrlService.getAll() } returns Flux.empty()
        every { jobUrlService.getAllNotFetched() } returns Flux.empty()
        every { rawPostService.findAllUnNotified() } returns Flux.just(rawPost)
        every { postService.findBySource("source") } returns Mono.just(post)
        every { communicationService.notify(any()) } returns Mono.error(Exception("failed to send message"))
        every { rawPostService.save(any()) } returns Mono.just(rawPost)

        mainSchedulers.start()

        assertSoftly {
            verify(exactly = 1) {
                postService.findBySource("source")
                communicationService.notify(post)
            }
            verify(exactly = 0) {
                rawPostService.save(any())
            }
        }
    }

    @Test
    fun `should update the post for jobSarkari`() {
        val url = "http://jobsarkari.com/job"
        val jobUrl = JobUrlBuilder(url = url).build()
        val rawPost = RawPostBuilder(source = url).build()
        val post = PostBuilder(source = url).build()

        every { dateProvider.getHour() } returns 0
        every { rojgarResultUrlFetcher.fetch() } returns Flux.empty()
        every { jobSarkariUrlFetcher.fetch() } returns Flux.empty()
        every { sarkariResultUrlFetcher.fetch() } returns Flux.empty()
        every { blockedJobUrlService.getAll() } returns Flux.empty()
        every { jobUrlService.getAllNotFetched() } returns Flux.just(jobUrl)
        every { jobSarkariPostFetcher.fetch(any()) } returns Mono.just(rawPost)
        every { jobUrlService.findByUrl(url) } returns Mono.just(jobUrl)
        every { jobUrlService.save(any()) } returns Mono.just(jobUrl)
        every { rawPostService.findAllUnNotified() } returns Flux.empty()

        mainSchedulers.start()

        val jobUrlSlot = slot<JobUrl>()

        assertSoftly {
            verify(exactly = 1) {
                blockedJobUrlService.getAll()
                jobUrlService.getAllNotFetched()
                jobSarkariPostFetcher.fetch(jobUrl)
                jobUrlService.findByUrl(post.source)
                jobUrlService.save(capture(jobUrlSlot))
            }
            jobUrlSlot.captured.status shouldBe true
        }
    }

    @Test
    fun `should update the post for rojgar result`() {
        val url = "http://rojgarresult.com/job"
        val jobUrl = JobUrlBuilder(url = url).build()
        val rawPost = RawPostBuilder(source = url).build()
        val post = PostBuilder(source = url).build()

        every { dateProvider.getHour() } returns 0
        every { rojgarResultUrlFetcher.fetch() } returns Flux.empty()
        every { jobSarkariUrlFetcher.fetch() } returns Flux.empty()
        every { sarkariResultUrlFetcher.fetch() } returns Flux.empty()
        every { blockedJobUrlService.getAll() } returns Flux.empty()
        every { jobUrlService.getAllNotFetched() } returns Flux.just(jobUrl)
        every { rojgarResultPostFetcher.fetch(any()) } returns Mono.just(rawPost)
        every { jobUrlService.findByUrl(url) } returns Mono.just(jobUrl)
        every { jobUrlService.save(any()) } returns Mono.just(jobUrl)
        every { rawPostService.findAllUnNotified() } returns Flux.empty()

        mainSchedulers.start()

        val jobUrlSlot = slot<JobUrl>()

        assertSoftly {
            verify(exactly = 1) {
                blockedJobUrlService.getAll()
                jobUrlService.getAllNotFetched()
                rojgarResultPostFetcher.fetch(jobUrl)
                jobUrlService.findByUrl(post.source)
                jobUrlService.save(capture(jobUrlSlot))
            }
            jobUrlSlot.captured.status shouldBe true
        }
    }

    @Test
    fun `should update the post for sarkari result`() {
        val url = "http://sarkariresults.info/job"
        val jobUrl = JobUrlBuilder(url = url).build()
        val rawPost = RawPostBuilder(source = url).build()
        val post = PostBuilder(source = url).build()

        every { dateProvider.getHour() } returns 0
        every { rojgarResultUrlFetcher.fetch() } returns Flux.empty()
        every { jobSarkariUrlFetcher.fetch() } returns Flux.empty()
        every { sarkariResultUrlFetcher.fetch() } returns Flux.empty()
        every { blockedJobUrlService.getAll() } returns Flux.empty()
        every { jobUrlService.getAllNotFetched() } returns Flux.just(jobUrl)
        every { sarkariResultPostFetcher.fetch(any()) } returns Mono.just(rawPost)
        every { jobUrlService.findByUrl(url) } returns Mono.just(jobUrl)
        every { jobUrlService.save(any()) } returns Mono.just(jobUrl)
        every { rawPostService.findAllUnNotified() } returns Flux.empty()

        mainSchedulers.start()

        val jobUrlSlot = slot<JobUrl>()

        assertSoftly {
            verify(exactly = 1) {
                blockedJobUrlService.getAll()
                jobUrlService.getAllNotFetched()
                sarkariResultPostFetcher.fetch(jobUrl)
                jobUrlService.findByUrl(post.source)
                jobUrlService.save(capture(jobUrlSlot))
            }
            jobUrlSlot.captured.status shouldBe true
        }
    }

    @Test
    fun `should not update the post if url is not valid for any source`() {
        val jobUrl = JobUrlBuilder(url = "http://url.com/job").build()
        val blockedUrl = BlockedJobUrlBuilder(url = "url").build()

        every { dateProvider.getHour() } returns 0
        every { rojgarResultUrlFetcher.fetch() } returns Flux.empty()
        every { jobSarkariUrlFetcher.fetch() } returns Flux.empty()
        every { sarkariResultUrlFetcher.fetch() } returns Flux.empty()
        every { blockedJobUrlService.getAll() } returns Flux.just(blockedUrl)
        every { jobUrlService.getAllNotFetched() } returns Flux.just(jobUrl)
        every { rawPostService.findAllUnNotified() } returns Flux.empty()

        mainSchedulers.start()

        assertSoftly {
            verify(exactly = 1) {
                blockedJobUrlService.getAll()
                jobUrlService.getAllNotFetched()
            }
            verify(exactly = 0) {
                jobUrlService.save(any())
                jobUrlService.findByUrl(any())
            }
        }
    }

    @Test
    fun `should not update the post if url is blocked`() {
        val url = "http://jobsarkari.com/job"
        val jobUrl = JobUrlBuilder(url = url).build()
        val blockedUrl = BlockedJobUrlBuilder(url = url).build()

        every { dateProvider.getHour() } returns 8
        every { jobUrlService.deleteAll() } returns Mono.empty()
        every { rojgarResultUrlFetcher.fetch() } returns Flux.empty()
        every { jobSarkariUrlFetcher.fetch() } returns Flux.empty()
        every { sarkariResultUrlFetcher.fetch() } returns Flux.empty()
        every { blockedJobUrlService.getAll() } returns Flux.just(blockedUrl)
        every { jobUrlService.getAllNotFetched() } returns Flux.just(jobUrl)
        every { rawPostService.findAllUnNotified() } returns Flux.empty()

        mainSchedulers.start()

        assertSoftly {
            verify(exactly = 1) {
                blockedJobUrlService.getAll()
                jobUrlService.getAllNotFetched()
            }
            verify(exactly = 0) {
                jobUrlService.save(any())
                jobSarkariPostFetcher.fetch(any())
                jobUrlService.findByUrl(any())
            }
        }
    }

}
