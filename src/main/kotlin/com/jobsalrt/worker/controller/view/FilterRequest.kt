package com.jobsalrt.worker.controller.view

import com.jobsalrt.worker.domain.FormType
import com.jobsalrt.worker.domain.Status
import com.jobsalrt.worker.domain.Type

data class FilterRequest(
    val status: List<Status> = emptyList(),
    val formType: List<FormType> = emptyList(),
    val type: List<Type> = emptyList()
)

