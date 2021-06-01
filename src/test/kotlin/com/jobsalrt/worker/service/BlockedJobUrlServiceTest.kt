package com.jobsalrt.worker.service

import com.jobsalrt.worker.domain.BlockedJobUrl
import com.jobsalrt.worker.repository.BlockedJobUrlRepository
import com.jobsalrt.worker.test_utils.assertNextWith
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux

class BlockedJobUrlServiceTest {
    private val blockedJobUrlRepository = mockk<BlockedJobUrlRepository>()
    private val blockedJobUrlService = BlockedJobUrlService(blockedJobUrlRepository)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `should get all blocked url`() {
        val blockedUrl = BlockedJobUrl(name = "url", url = "http://url.com/url")
        val blockedUrl1 = BlockedJobUrl(name = "url1", url = "http://url.com/url1")

        every { blockedJobUrlRepository.findAll() } returns Flux.just(blockedUrl, blockedUrl1)

        val result = blockedJobUrlService.getAll()

        assertNextWith(
            result,
            {
                it shouldBe blockedUrl
            },
            {
                it shouldBe blockedUrl1
            }
        )
    }
}
