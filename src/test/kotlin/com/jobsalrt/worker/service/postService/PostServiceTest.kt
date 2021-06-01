package com.jobsalrt.worker.service.postService

import com.jobsalrt.worker.builder.BasicDetailsBuilder
import com.jobsalrt.worker.builder.PostBuilder
import com.jobsalrt.worker.controller.view.FilterRequest
import com.jobsalrt.worker.repository.PostRepository
import com.jobsalrt.worker.repository.PostRepositoryOps
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

class PostServiceTest {
    private val postRepository = mockk<PostRepository>()
    private val postRepositoryOps = mockk<PostRepositoryOps>()
    private val postService = PostService(postRepository, postRepositoryOps)

    @BeforeEach
    fun setUp() {
        clearAllMocks()
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `should save post`() {
        val post = PostBuilder().build()
        every { postRepository.save(any()) } returns Mono.just(post)

        val result = postService.save(post)
        assertNextWith(result) {
            it shouldBe post
            verify(exactly = 1) {
                postRepository.save(post)
            }
        }
    }

    @Test
    fun `should find post by given source`() {
        val post = PostBuilder(source = "source").build()
        every { postRepository.findBySource("source") } returns Mono.just(post)

        val result = postService.findBySource("source")
        assertNextWith(result) {
            it shouldBe post
            verify(exactly = 1) {
                postRepository.findBySource("source")
            }
        }
    }

    @Test
    fun `should find all posts based on filters`() {
        val post = PostBuilder(source = "source").build()
        val post1 = PostBuilder(source = "source1").build()
        every { postRepositoryOps.findPosts(any(), any()) } returns Flux.just(post, post1)

        val filterRequest = FilterRequest(emptyMap())
        val result = postService.getAllPosts(1, filterRequest)
        assertNextWith(result) {
            it shouldBe listOf(post, post1)
            verify(exactly = 1) {
                postRepositoryOps.findPosts(filterRequest, 1)
            }
        }
    }

    @Test
    fun `should get post page count`() {
        every { postRepositoryOps.findPostCount(any()) } returns Mono.just(Pair(10, 1.0))

        val filterRequest = FilterRequest(emptyMap())
        val result = postService.getPostsPageCount(filterRequest)
        assertNextWith(result) {
            it.first shouldBe 10
            it.second shouldBe 1.0
            verify(exactly = 1) {
                postRepositoryOps.findPostCount(filterRequest)
            }
        }
    }

    @Test
    fun `should get post by post url`() {
        val post = PostBuilder(basicDetails = BasicDetailsBuilder(url = "url").build()).build()

        every { postRepositoryOps.findByBasicDetailsUrl(any()) } returns Mono.just(post)

        val result = postService.getPostByUrl("url")
        assertNextWith(result) {
            it shouldBe post
            verify(exactly = 1) {
                postRepositoryOps.findByBasicDetailsUrl("url")
            }
        }
    }

    @Test
    fun `should update post`() {
        val post = PostBuilder(basicDetails = BasicDetailsBuilder(url = "url").build()).build()
        val post1 = PostBuilder(basicDetails = BasicDetailsBuilder(url = "url1").build()).build()

        every { postRepositoryOps.findByBasicDetailsUrl(any()) } returns Mono.just(post)
        every { postRepository.save(any()) } returns Mono.just(post1)

        val result = postService.updatePost("url", post1)
        assertNextWith(result) {
            it shouldBe post1
            verify(exactly = 1) {
                postRepositoryOps.findByBasicDetailsUrl("url")
                postRepository.save(post1)
            }
        }
    }

    @Test
    fun `should give true if unique url is available`() {
        every { postRepositoryOps.findByBasicDetailsUrl(any()) } returns Mono.empty()

        val result = postService.urlAvailable("url")
        assertNextWith(result) {
            it.first shouldBe true
            it.second shouldBe ""
            verify(exactly = 1) {
                postRepositoryOps.findByBasicDetailsUrl("url")
            }
        }
    }

    @Test
    fun `should give false if unique url is available`() {
        val post = PostBuilder(source = "source", basicDetails = BasicDetailsBuilder(url = "url").build()).build()

        every { postRepositoryOps.findByBasicDetailsUrl(any()) } returns Mono.just(post)

        val result = postService.urlAvailable("url")
        assertNextWith(result) {
            it.first shouldBe false
            it.second shouldBe "source"
            verify(exactly = 1) {
                postRepositoryOps.findByBasicDetailsUrl("url")
            }
        }
    }

    @Test
    fun `should delete post by given url`() {
        val post = PostBuilder(basicDetails = BasicDetailsBuilder(url = "url").build()).build()

        every { postRepository.deleteByBasicDetailsUrl(any()) } returns Mono.just(post)

        val result = postService.deletePostByUrl("url")
        assertNextWith(result) {
            it shouldBe post
            verify(exactly = 1) {
                postRepository.deleteByBasicDetailsUrl("url")
            }
        }
    }
}
