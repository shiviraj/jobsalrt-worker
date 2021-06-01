package com.jobsalrt.worker.utils

import org.springframework.stereotype.Component
import java.time.LocalDateTime

@Component
class DateProvider {
    fun getHour(): Int {
        return LocalDateTime.now().hour
    }
}
