package com.jobsalrt.worker.service.postService

import com.jobsalrt.worker.domain.Post
import com.jobsalrt.worker.service.jobSarkari.JobSarkariPostFetcher
import com.jobsalrt.worker.service.rojgarResult.RojgarResultPostFetcher
import com.jobsalrt.worker.service.sarkariResult.SarkariResultPostFetcher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class UpdateFetcher(
    @Autowired private val jobSarkariPostFetcher: JobSarkariPostFetcher,
    @Autowired private val rojgarResultPostFetcher: RojgarResultPostFetcher,
    @Autowired private val sarkariResultPostFetcher: SarkariResultPostFetcher,
    @Autowired private val postService: PostService
) {
    fun fetchUpdate(url: String): Mono<Post> {
        return postService.getPostByUrl(url)
            .flatMap {
                val source = it.source
                when {
                    isContains(source, "jobsarkari.com") -> jobSarkariPostFetcher.fetchPost(source)
                    isContains(source, "rojgarresult.com") -> rojgarResultPostFetcher.fetchPost(source)
                    isContains(source, "sarkariresults.info") -> sarkariResultPostFetcher.fetchPost(source)
                    else -> Mono.empty()
                }
            }
    }

    private fun isContains(source: String, pattern: String) = source.contains(Regex(pattern, RegexOption.IGNORE_CASE))
}
