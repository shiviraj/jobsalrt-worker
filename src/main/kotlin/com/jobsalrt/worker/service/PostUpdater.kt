package com.jobsalrt.worker.service

import com.jobsalrt.worker.domain.JobUrl
import com.jobsalrt.worker.domain.JobUrlStatus
import com.jobsalrt.worker.service.jobSarkari.JobSarkariPostFetcher
import com.jobsalrt.worker.service.rojgarResult.RojgarResultPostFetcher
import com.jobsalrt.worker.service.sarkariResult.SarkariResultPostFetcher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux

@Service
class PostUpdater(
    @Autowired val blockedJobUrlService: BlockedJobUrlService,
    @Autowired val jobUrlService: JobUrlService,
    @Autowired val jobSarkariPostFetcher: JobSarkariPostFetcher,
    @Autowired val rojgarResultPostFetcher: RojgarResultPostFetcher,
    @Autowired val sarkariResultPostFetcher: SarkariResultPostFetcher
) {
    fun updatePosts(): Flux<JobUrl> {
        val blockedJobUrls = blockedJobUrlService.getAll().collectList().block()!!
        return jobUrlService.getAllNotFetched()
            .filter { jobUrl ->
                !blockedJobUrls.any { it.url == jobUrl.url }
            }
            .flatMap { jobUrl ->
                getPostFetcher(jobUrl)
                    .fetch(jobUrl)
                    .flatMap {
                        jobUrl.status = JobUrlStatus.FETCHED
                        jobUrlService.save(jobUrl)
                    }
            }
            .onErrorContinue { throwable, u ->
                println(u)
                throwable.printStackTrace()
            }
    }

    private fun getPostFetcher(it: JobUrl) = when {
        isContains(it, "jobsarkari.com") -> jobSarkariPostFetcher
        isContains(it, "rojgarresult.com") -> rojgarResultPostFetcher
        else -> sarkariResultPostFetcher
    }

    private fun isContains(it: JobUrl, pattern: String) = it.url.contains(Regex(pattern, RegexOption.IGNORE_CASE))
}
