package com.jobsalrt.worker.service

import com.jobsalrt.worker.domain.JobUrl
import com.jobsalrt.worker.service.jobSarkari.JobSarkariUrlFetcher
import com.jobsalrt.worker.service.rojgarResult.RojgarResultUrlFetcher
import com.jobsalrt.worker.service.sarkariResult.SarkariResultUrlFetcher
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.util.function.Tuple3

@Service
class UrlService(
    @Autowired val rojgarResultUrlFetcher: RojgarResultUrlFetcher,
    @Autowired val jobSarkariUrlFetcher: JobSarkariUrlFetcher,
    @Autowired val sarkariResultUrlFetcher: SarkariResultUrlFetcher
) {
    fun fetchUrls(): Flux<Tuple3<JobUrl, JobUrl, JobUrl>> {
        return Flux.zip(
            rojgarResultUrlFetcher.fetch(),
            jobSarkariUrlFetcher.fetch(),
            sarkariResultUrlFetcher.fetch()
        )
    }
}
