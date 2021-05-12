package com.jobsalrt.worker.controller.view

import com.jobsalrt.worker.domain.FormType
import com.jobsalrt.worker.domain.Status
import com.jobsalrt.worker.domain.Type

data class FilterRequest(
    val status: List<Status> = emptyList(),
    val formType: List<FormType> = emptyList(),
    val type: List<Type> = emptyList()
) {
    fun getRegex(): Map<String, String> {
        return mapOf(
            "status" to status.joinToString("|"),
            "formType" to formType.joinToString("|"),
            "type" to type.joinToString("|")
        )
    }

    fun createQuery(): String {
        val status = status.joinToString("|")
        val formType = formType.joinToString("|")
        val type = type.joinToString("|")
        var query = ""
        if (status != "") query += "status : {\$regex : '$status'},"
        if (formType != "") query += "'basicDetails.formType' : {\$regex : '$formType'},"
//        if (type != "") sb.append("'states.type' : {\$regex : $type},")
        return query
    }
}

