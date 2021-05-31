package com.jobsalrt.worker.service

import com.jobsalrt.worker.builder.JobUrlBuilder
import com.jobsalrt.worker.repository.JobUrlRepository
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldHaveSize
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class JobUrlServiceTest(
    @Autowired private val jobUrlRepository: JobUrlRepository
) {

    private val jobUrlService = JobUrlService(jobUrlRepository)
    private val url = JobUrlBuilder(name = "name", url = "url", isFetched = true).build()
    private val url1 = JobUrlBuilder(name = "name1", url = "url1").build()

    @BeforeEach
    fun setUp() {
        jobUrlRepository.deleteAll().block()
        jobUrlRepository.saveAll(listOf(url, url1)).blockLast()
    }

    @AfterEach
    fun tearDown() {
        jobUrlRepository.deleteAll().block()
    }

    @Test
    fun `should save url`() {
        val url = JobUrlBuilder(name = "jobUrl", url = "jobUrl").build()

        jobUrlService.save(url).block()

        assertSoftly {
            url shouldBe jobUrlRepository.findByUrl("jobUrl").block()
        }
    }

    @Test
    fun `should get all urls which are not fetched`() {
        val urls = jobUrlService.getAllNotFetched().collectList().block()

        assertSoftly {
            urls shouldBe listOf(url1)
        }
    }

    @Test
    fun `should find job url by url`() {
        val jobUrl = jobUrlService.findByUrl("url").block()

        assertSoftly {
            jobUrl shouldBe url
        }
    }

    @Test
    fun `should delete all job urls`() {
        jobUrlService.deleteAll().block()

        val urlsInDB = jobUrlRepository.findAll().toIterable().toList()
        assertSoftly {
            urlsInDB shouldHaveSize 0
        }
    }
}
