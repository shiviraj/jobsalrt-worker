package com.jobsalrt.worker.schedulers

import com.jobsalrt.worker.domain.JobUrl
import com.jobsalrt.worker.domain.JobUrlStatus
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
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.scheduler.Schedulers
import reactor.util.function.Tuple3

@Component
class MainSchedulers(
    @Autowired private val jobUrlService: JobUrlService,
    @Autowired private val jobSarkariUrlFetcher: JobSarkariUrlFetcher,
    @Autowired private val rojgarResultUrlFetcher: RojgarResultUrlFetcher,
    @Autowired private val sarkariResultUrlFetcher: SarkariResultUrlFetcher,
    @Autowired private val jobSarkariPostFetcher: JobSarkariPostFetcher,
    @Autowired private val rojgarResultPostFetcher: RojgarResultPostFetcher,
    @Autowired private val sarkariResultPostFetcher: SarkariResultPostFetcher,
    @Autowired private val blockedJobUrlService: BlockedJobUrlService,
    @Autowired private val communicationService: CommunicationService,
    @Autowired private val rawPostService: RawPostService,
    @Autowired private val postService: PostService,
    @Autowired private val dateProvider: DateProvider
) {
    @Scheduled(cron = "0 0/15 * * * *")
    fun start() {
        if (dateProvider.getHour() == 8 && dateProvider.getMinute() == 0)
            jobUrlService.deleteAll().block()
        if (dateProvider.getMinute() == 0)
            fetchUrls().subscribeOn(Schedulers.boundedElastic()).blockLast()
        updatePosts().subscribeOn(Schedulers.boundedElastic()).blockLast()
        sendNotification().subscribeOn(Schedulers.boundedElastic()).blockLast()
    }

    private fun sendNotification(): Flux<RawPost> {
        return rawPostService.findAllUnNotified()
            .flatMap { rawPost ->
                postService.findBySource(rawPost.source)
                    .flatMap { post ->
                        communicationService.notify(post)
                            .flatMap {
                                rawPost.isNotified = true
                                rawPostService.save(rawPost)
                            }
                    }
            }
    }

    private fun updatePosts(): Flux<JobUrl> {
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

    private fun fetchUrls(): Flux<Tuple3<JobUrl, JobUrl, JobUrl>> {
        return Flux.zip(
            rojgarResultUrlFetcher.fetch(),
            jobSarkariUrlFetcher.fetch(),
            sarkariResultUrlFetcher.fetch()
        )
    }
}
