package com.jobsalrt.worker.controller.view

data class FilterRequest(
    val filters: Map<String, List<Any>>,
    val sortOrder: String = "desc",
    val sortBy: String = "createdAt",
    val search: String = ""
)

