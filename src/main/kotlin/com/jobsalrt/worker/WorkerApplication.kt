package com.jobsalrt.worker

import com.jobsalrt.worker.service.JobUrlService
import com.jobsalrt.worker.service.Notifier
import com.jobsalrt.worker.service.PostUpdater
import com.jobsalrt.worker.service.UrlService
import com.jobsalrt.worker.utils.DateProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.builder.SpringApplicationBuilder
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.ApplicationContext
import org.springframework.data.mongodb.config.EnableMongoAuditing
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.EnableTransactionManagement
import java.util.*
import kotlin.system.exitProcess


@SpringBootApplication
@EnableMongoAuditing
@EnableScheduling
@EnableTransactionManagement
@ConfigurationPropertiesScan
class WorkerApplication {
    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            val properties = Properties()
            properties["spring.data.mongodb.uri"] = System.getenv("MONGODB_URL")
            SpringApplicationBuilder(WorkerApplication::class.java).properties(properties).run(*args)
        }
    }
}

@Component
class CommandLineAppStartupRunner(
    @Autowired val appContext: ApplicationContext,
    @Autowired val dateProvider: DateProvider,
    @Autowired val jobUrlService: JobUrlService,
    @Autowired val urlService: UrlService,
    @Autowired val postUpdater: PostUpdater,
    @Autowired val notifier: Notifier

) : CommandLineRunner {
    override fun run(vararg args: String) {
        if (dateProvider.getHour() == 0)
            jobUrlService.deleteAll().block()

        if (dateProvider.getMinute() <= 20)
            urlService.fetchUrls().blockLast()
        postUpdater.updatePosts().blockLast()
        notifier.notify().blockLast()
        SpringApplication.exit(appContext)
        exitProcess(0)
    }
}
