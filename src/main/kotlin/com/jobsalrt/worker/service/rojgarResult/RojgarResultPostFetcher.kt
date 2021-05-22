package com.jobsalrt.worker.service.rojgarResult

import com.jobsalrt.worker.domain.BasicDetails
import com.jobsalrt.worker.domain.Details
import com.jobsalrt.worker.domain.FormType
import com.jobsalrt.worker.service.postService.PostFetcher
import com.jobsalrt.worker.service.postService.PostService
import com.jobsalrt.worker.service.postService.RawPostService
import com.jobsalrt.worker.webClient.WebClientWrapper
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class RojgarResultPostFetcher(
    @Autowired webClientWrapper: WebClientWrapper,
    @Autowired postService: PostService,
    @Autowired rawPostService: RawPostService
) :
    PostFetcher(webClientWrapper, postService, rawPostService) {
    override fun parseHtml(document: Document): String {
        return getMainTable(document).toString()
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

    override fun getImportantLinks(document: Document): Details {
        val tableData = getMainTable(document)?.select("tr")?.toList() ?: emptyList()

        val startIndex = tableData.indexOfFirst {
            it.select("h2").text().contains(Regex("important link", RegexOption.IGNORE_CASE))
        }

        val endIndex = tableData.subList(startIndex + 1, tableData.size).indexOfFirst {
            it.select("td").select("h2").text().contains(Regex("official website", RegexOption.IGNORE_CASE))
        } + startIndex + 2

        return parseImportantLinks(tableData.subList(startIndex + 1, endIndex))
    }

    override fun getHowToApplyDetails(document: Document): List<String>? {
        return findTableData(document, "how to apply")
            ?.select("li")
            ?.map {
                it.text().trim()
            }
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

    override fun getBasicDetails(document: Document): BasicDetails {
        val subList = getMainTable(document)?.select("td")?.toList()?.subList(0, 3) ?: emptyList()
        val name = subList.first().text().trim()
        val totalPost = subList[2].text().split(" ")[1].trim()
        val advtNo = try {
            subList[1].select("h2")[1].text().trim().split(":")[1].trim()
        } catch (e: Exception) {
            null
        }

        return BasicDetails(
            name = name,
            formType = FormType.ONLINE,
            advtNo = advtNo,
            totalVacancies = totalPost.toLong(),
        )

    }

    private fun findTableData(document: Document, regexPattern: String): Element? {
        return getMainTable(document)?.select("td")
            ?.toList()
            ?.find {
                it.text().contains(Regex(regexPattern, RegexOption.IGNORE_CASE))
            }
    }

    private fun getMainTable(document: Document): Element? {
        return if (document.select("#table").text() != "")
            document.select("#table")[0]
        else document.select("table")[0]
    }

    private fun getTableOfMultipleRows(document: Document, regexPattern: String): List<Element> {
        val tableData = getMainTable(document)?.select("tr")
            ?.toList() ?: emptyList()
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
            val body = getTableOfMultipleRows(document, regexPattern)
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
