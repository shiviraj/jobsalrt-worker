package com.jobsalrt.worker.service

import com.jobsalrt.worker.builder.AdminBuilder
import com.jobsalrt.worker.controller.view.AuthenticationRequest
import com.jobsalrt.worker.repository.AdminRepository
import com.jobsalrt.worker.test_utils.assertNextWith
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.shouldBe
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.bson.types.ObjectId
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class AdminServiceTest {
    private val adminRepository = mockk<AdminRepository>()
    private val adminService = AdminService(adminRepository)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `should login admin with correct credentials`() {
        val admin = AdminBuilder(name = "Shiviraj", email = "shivi@raj.com").build()

        every { adminRepository.findByEmail("shivi@raj.com") } returns Mono.just(admin)

        val login = adminService.login(AuthenticationRequest(email = "shivi@raj.com", password = "password"))

        assertNextWith(login) {
            it shouldBe admin
        }
    }

    @Test
    fun `should not login admin with incorrect credentials`() {
        val admin = AdminBuilder(name = "Shiviraj", email = "shivi@raj.com").build()

        every { adminRepository.findByEmail("shivi@raj.com") } returns Mono.just(admin)

        val login = adminService.login(AuthenticationRequest(email = "shivi@raj.com", password = "pass"))

        StepVerifier.create(login).verifyComplete()
    }

    @Test
    fun `should load user by username as email`() {
        val admin = AdminBuilder(name = "Shiviraj", email = "shivi@raj.com").build()
        every { adminRepository.findByEmail("shivi@raj.com") } returns Mono.just(admin)

        val userDetails = adminService.loadUserByUsername("shivi@raj.com")

        assertSoftly {
            userDetails.username shouldBe admin.email
            userDetails.password shouldBe admin.password
        }
    }

    @Test
    fun `should load null user by incorrect username as email`() {
        every { adminRepository.findByEmail("shivi@email.com") } returns Mono.empty()
        val userDetails = adminService.loadUserByUsername("shivi@email.com")

        assertSoftly {
            userDetails.username shouldBe "email"
            userDetails.password shouldBe "password"
        }
    }

    @Test
    fun `should get admin by email`() {
        val admin = AdminBuilder(name = "Shiviraj", email = "shivi@raj.com").build()

        every { adminRepository.findByEmail("shivi@raj.com") } returns Mono.just(admin)

        val adminDetails = adminService.getAdminByEmail("shivi@raj.com")
        assertNextWith(adminDetails) {
            it shouldBe admin
        }
    }

    @Test
    fun `should save admin in repository`() {
        val admin = AdminBuilder(id = ObjectId("60b3a48e4a147a7836bc6387"), email = "example@email.com").build()
        every { adminRepository.save(any()) } returns Mono.just(admin)
        val adminDetails = adminService.save(admin)

        assertNextWith(adminDetails) {
            it shouldBe admin
            verify(exactly = 1) {
                adminRepository.save(admin)
            }
        }
    }

    @Test
    fun `should get admin by token`() {
        val admin = AdminBuilder(name = "Shiviraj", email = "shivi@raj.com").build()

        every { adminRepository.findByToken(admin.token!!) } returns Mono.just(admin)

        val adminDetails = adminService.getAdminByToken(admin.token!!)
        assertNextWith(adminDetails) {
            it shouldBe admin
        }
    }
}
