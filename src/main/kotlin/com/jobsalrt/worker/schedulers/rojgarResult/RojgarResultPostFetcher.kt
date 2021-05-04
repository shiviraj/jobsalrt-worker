package com.jobsalrt.worker.schedulers.rojgarResult

import com.jobsalrt.worker.domain.BasicDetails
import com.jobsalrt.worker.domain.Details
import com.jobsalrt.worker.domain.JobUrl
import com.jobsalrt.worker.domain.Post
import com.jobsalrt.worker.schedulers.PostFetcher
import com.jobsalrt.worker.service.PostService
import com.jobsalrt.worker.webClient.WebClientWrapper
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class RojgarResultPostFetcher(webClientWrapper: WebClientWrapper, postService: PostService) :
    PostFetcher(webClientWrapper, postService) {

    fun fetchPost(jobUrl: JobUrl): Mono<Post> {
        return fetch(jobUrl)
    }

    override fun getOtherDetails(document: Document): Map<String, Details> {
        val tableData = document.select("#table").select("tr").toList()

        val lastIndex = tableData.indexOfFirst {
            it.select("h2").text().contains(Regex("rojgarresult.com", RegexOption.IGNORE_CASE))
        }

        val map = mutableMapOf<String, Details>()

        tableData.subList(3, lastIndex)
            .filter {
                val regexPattern =
                    "(important date|application fee|vacancy detail|age limit|selection process|how to apply|important link)"
                val text = it.select("h2").text()
                text != "" && !text.contains(Regex(regexPattern, RegexOption.IGNORE_CASE))
            }
            .map {
                val tableName = it.select("h2").text().trim()
                val details = createDetails(document, tableName)
                map[tableName] = details
            }

        return map
    }

    override fun getImportantLinks(document: Document): Details? {
        val tableData = document.select("#table").select("tr").toList()

        val startIndex = tableData.indexOfFirst {
            it.select("h2").text().contains(Regex("important link", RegexOption.IGNORE_CASE))
        }

        val endIndex = tableData.subList(startIndex + 1, tableData.size).indexOfFirst {
            it.select("td").select("h2").text().contains(Regex("official website", RegexOption.IGNORE_CASE))
        } + startIndex + 2

        val body = tableData.subList(startIndex + 1, endIndex)
            .map {
                val list = it.select("td").toList()
                listOf(
                    list.first().text().trim(),
                    list[1].select("a").attr("href").trim()
                )
            }
        return Details(body = body)
    }

    override fun getHowToApplyDetails(document: Document): List<String>? {
        return null
    }

    override fun getSelectionProcessDetails(document: Document): List<String>? {
        return findTableData(document, "selection procedure")
            ?.select("li")
            ?.map {
                it.text().trim()
            }
    }

    override fun getAgeLimitDetails(document: Document): Details? {
        return createDetails(document, "age limit")
    }

    override fun getVacancyDetails(document: Document): Details? {
        return createDetails(document, "vacancy detail")
    }

    override fun getFeeDetails(document: Document): Details? {
        return createDetails(document, "application fee")
    }

    override fun getDates(document: Document): Details? {
        return createDetails(document, "important date", true)
    }

    override fun getBasicDetails(document: Document): BasicDetails? {
        return null
    }

    private fun findTableData(document: Document, regexPattern: String): Element? {
        return document.select("#table").select("td")
            .toList()
            .find {
                it.select("h2").text().contains(Regex(regexPattern, RegexOption.IGNORE_CASE))
            }
    }

    private fun getTable(document: Document, regexPattern: String): List<Element> {
        val tableData = document.select("#table").select("tr")
            .toList()
        val startIndex = tableData.indexOfFirst {
            it.select("h2").text().contains(Regex(regexPattern, RegexOption.IGNORE_CASE))
        }

        val endIndex = tableData.subList(startIndex + 1, tableData.size).indexOfFirst {
            it.select("td").select("h2").text() != ""
        } + startIndex + 1
        return tableData.subList(startIndex + 1, endIndex)
    }

    private fun createDetails(document: Document, regexPattern: String, skipHeader: Boolean = false): Details {
        val select = findTableData(document, regexPattern)
            ?.select("li")
        return if (select?.text() != "") {
            val body = select?.toList()
                ?.map { element ->
                    element.text().split(":").map { it.trim() }
                }
            Details(body = body!!)
        } else {
            val body = getTable(document, regexPattern)
                .map { e ->
                    e.select("td").toList()
                        .map { element ->
                            element.text().trim()
                        }
                }
            if (skipHeader)
                Details(body = body)
            else
                Details(header = body.first(), body = body.subList(1, body.size))
        }
    }
}
