package com.jobsalrt.worker.service

import com.jobsalrt.worker.domain.BlockedJobUrl
import com.jobsalrt.worker.repository.BlockedJobUrlRepository
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest
class BlockedJobUrlServiceTest(
    @Autowired private val blockedJobUrlRepository: BlockedJobUrlRepository
) {

    private val blockedJobUrlService = BlockedJobUrlService(blockedJobUrlRepository)

    @BeforeEach
    fun setUp() {
        blockedJobUrlRepository.deleteAll().block()
    }

    @AfterEach
    fun tearDown() {
        blockedJobUrlRepository.deleteAll().block()
    }

    @Test
    fun `should get all blocked url`() {
        val blockedUrl = BlockedJobUrl(name = "url", url = "http://url.com/url")
        val blockedUrl1 = BlockedJobUrl(name = "url1", url = "http://url.com/url1")
        blockedJobUrlRepository.saveAll(listOf(blockedUrl, blockedUrl1)).blockLast()

        val result = blockedJobUrlService.getAll().toIterable().toList()

        assertSoftly {
            result shouldBe listOf(blockedUrl, blockedUrl1)
        }
    }
}
