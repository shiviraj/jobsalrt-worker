package com.jobsalrt.worker.schedulers

import com.jobsalrt.worker.domain.JobUrl
import com.jobsalrt.worker.domain.Post
import com.jobsalrt.worker.schedulers.jobSarkari.JobSarkariPostFetcher
import com.jobsalrt.worker.schedulers.jobSarkari.JobSarkariUrlFetcher
import com.jobsalrt.worker.schedulers.rojgarResult.RojgarResultPostFetcher
import com.jobsalrt.worker.schedulers.rojgarResult.RojgarResultUrlFetcher
import com.jobsalrt.worker.schedulers.sarkariResult.SarkariResultPostFetcher
import com.jobsalrt.worker.schedulers.sarkariResult.SarkariResultUrlFetcher
import com.jobsalrt.worker.service.JobUrlService
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.util.function.Tuple3

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
    @Scheduled(cron = " 0/5 * * * * *")
    @SchedulerLock(name = "MainSchedulers_start", lockAtLeastFor = "5m", lockAtMostFor = "5m")
    fun start() {
//        val second = LocalDateTime.now().second
//        if (second == 0)
//        fetchUrls().subscribe()
//        else
        updatePosts().subscribe()
    }

    private fun updatePosts(): Mono<Post> {
        return jobUrlService.findByUrl("https://sarkariresults.info/2021/bihar-btsc-medical-officer.php")
//        getAllNotFetched()
//            .filter {
//                it.url.contains(Regex("(sarkariresult)", RegexOption.IGNORE_CASE))
//            }
            .flatMap {
                sarkariResultPostFetcher.fetch(it)
//            }.flatMap {
//                jobUrlService.findByUrl(it.source)
//                    .flatMap { jobUrl ->
//                        jobUrl.isFetched = true
//                        jobUrlService.save(jobUrl)
//                    }
            }
    }

    private fun fetchUrls(): Flux<Tuple3<JobUrl, JobUrl, JobUrl>> {
        return Flux.zip(
            rojgarResultUrlFetcher.fetch(),
            jobSarkariUrlFetcher.fetch(),
            sarkariResultUrlFetcher.fetch()
        )
    }
}
