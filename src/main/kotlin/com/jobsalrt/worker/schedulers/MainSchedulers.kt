package com.jobsalrt.worker.schedulers

import com.jobsalrt.worker.domain.JobUrl
import com.jobsalrt.worker.schedulers.jobSarkari.JobSarkariPostFetcher
import com.jobsalrt.worker.schedulers.jobSarkari.JobSarkariUrlFetcher
import com.jobsalrt.worker.schedulers.rojgarResult.RojgarResultPostFetcher
import com.jobsalrt.worker.schedulers.rojgarResult.RojgarResultUrlFetcher
import com.jobsalrt.worker.schedulers.sarkariResult.SarkariResultPostFetcher
import com.jobsalrt.worker.schedulers.sarkariResult.SarkariResultUrlFetcher
import com.jobsalrt.worker.service.JobUrlService
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.dao.DuplicateKeyException
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.function.Tuple3
import java.time.LocalDateTime

@Component
class MainSchedulers(
    @Autowired private val jobUrlService: JobUrlService,
    @Autowired private val jobSarkariUrlFetcher: JobSarkariUrlFetcher,
    @Autowired private val rojgarResultUrlFetcher: RojgarResultUrlFetcher,
    @Autowired private val sarkariResultUrlFetcher: SarkariResultUrlFetcher,
    @Autowired private val jobSarkariPostFetcher: JobSarkariPostFetcher,
    @Autowired private val rojgarResultPostFetcher: RojgarResultPostFetcher,
    @Autowired private val sarkariResultPostFetcher: SarkariResultPostFetcher
) {
    @Scheduled(cron = "0 0/30 * * * *")
    @SchedulerLock(name = "MainSchedulers_start", lockAtLeastFor = "5m", lockAtMostFor = "5m")
    fun start() {
        if (LocalDateTime.now().hour == 8) jobUrlService.deleteAll().subscribe()
        if (LocalDateTime.now().minute == 0)
            fetchUrls()
                .onErrorResume {
                    if (it is DuplicateKeyException)
                        Flux.empty()
                    else throw it
                }.subscribe()
        else
            updatePosts()
                .onErrorResume {
                    if (it is DuplicateKeyException)
                        Flux.empty()
                    else throw it
                }.subscribe()
    }

    private fun updatePosts(): Flux<JobUrl> {
        return jobUrlService.getAllNotFetched()
            .flatMap {
                when {
                    isContains(it, "jobsarkari.com") -> jobSarkariPostFetcher.fetch(it)
                    isContains(it, "rojgarresult.com") -> rojgarResultPostFetcher.fetch(it)
                    isContains(it, "sarkariresults.info") -> sarkariResultPostFetcher.fetch(it)
                    else -> Mono.empty()
                }
            }.flatMap {
                jobUrlService.findByUrl(it.source)
                    .flatMap { jobUrl ->
                        jobUrl.isFetched = true
                        jobUrlService.save(jobUrl)
                    }
            }
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
