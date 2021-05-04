package com.jobsalrt.worker.schedulers

import com.jobsalrt.worker.domain.JobUrl
import com.jobsalrt.worker.schedulers.jobSarkari.JobSarkariUrlFetcher
import com.jobsalrt.worker.schedulers.rojgarResult.RojgarResultUrlFetcher
import com.jobsalrt.worker.schedulers.sarkariResult.SarkariResultUrlFetcher
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.util.function.Tuple3

@Component
class MainSchedulers(
    @Autowired private val jobSarkariUrlFetcher: JobSarkariUrlFetcher,
    @Autowired private val rojgarResultUrlFetcher: RojgarResultUrlFetcher,
    @Autowired private val sarkariResultUrlFetcher: SarkariResultUrlFetcher
) {
    @Scheduled(cron = "0/10 * * * * *")
    @SchedulerLock(name = "MainSchedulers_start", lockAtLeastFor = "5m", lockAtMostFor = "5m")
    fun start() {
        fetchUrls().subscribe()
    }

    private fun fetchUrls(): Flux<Tuple3<JobUrl, JobUrl, JobUrl>> {
        return Flux.zip(
            rojgarResultUrlFetcher.fetch(),
            jobSarkariUrlFetcher.fetch(),
            sarkariResultUrlFetcher.fetch()
        )

    }
}
