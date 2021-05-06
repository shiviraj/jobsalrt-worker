package com.jobsalrt.worker.schedulers.sarkariResult

import com.jobsalrt.worker.domain.BasicDetails
import com.jobsalrt.worker.domain.Details
import com.jobsalrt.worker.domain.FormType
import com.jobsalrt.worker.schedulers.PostFetcher
import com.jobsalrt.worker.service.CommunicationService
import com.jobsalrt.worker.service.PostService
import com.jobsalrt.worker.webClient.WebClientWrapper
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service

@Service
class SarkariResultPostFetcher(
    @Autowired webClientWrapper: WebClientWrapper,
    @Autowired postService: PostService,
    @Autowired communicationService: CommunicationService
) : PostFetcher(webClientWrapper, postService, communicationService) {
    override fun getOtherDetails(document: Document): Map<String, Details>? {
        val tableData = findMainTable(document).select("tr").toList()

        val lastIndex = tableData.indexOfFirst {
            it.select("h2").text().contains(Regex("important link", RegexOption.IGNORE_CASE))
        } - 2

        val map = mutableMapOf<String, Details>()
        val regexPattern =
            "(important date|application fee|vacancy detail|age limit|selection process|how to apply|important link)"

        tableData.subList(1, lastIndex)
            .filter { element ->
                listOf("h2", "h3", "h4").any {
                    val text = element.select(it).text()
                    text != "" && !text.contains(Regex(regexPattern, RegexOption.IGNORE_CASE))
                }
            }
            .map {
                val tableName = it.select("h3").text().trim()
                map[tableName] = createDetails(document, tableName)
            }

        return map
    }

    override fun getImportantLinks(document: Document): Details? {
        val tableData = findMainTable(document).select("tr")
            ?.toList() ?: emptyList()
        val startIndex = tableData.indexOfFirst {
            it.select("h2").text().contains(Regex("important link", RegexOption.IGNORE_CASE))
        }

        val endIndex = tableData.subList(startIndex + 1, tableData.size).indexOfFirst {
            it.select("td").select("h4").text().contains(Regex("official website", RegexOption.IGNORE_CASE))
        } + startIndex + 2

        val body = tableData.subList(startIndex + 1, endIndex)
            .map {
                val list = it.select("td").toList()
                listOf(
                    list.first().text().trim(),
                    list[1].select("a").attr("href").trim()
//                    list[1].select("a").toList()
//                        .map { anchorTag ->
//                            Pair(anchorTag.text().trim(), anchorTag.attr("href").trim())
//                        }
                )
            }
        return Details(body = body)
    }

    override fun getHowToApplyDetails(document: Document): List<String>? {
        return findDataFromRegex(document, "how to apply")
            ?.select("li")
            ?.toList()
            ?.map {
                it.text().trim()
            }
    }

    override fun getSelectionProcessDetails(document: Document): List<String>? {
        return findDataFromRegex(document, "selection process")
            ?.select("li")
            ?.toList()
            ?.map {
                it.text().trim()
            }
    }

    override fun getAgeLimitDetails(document: Document): Details? {
        val ageLimitTable = findDataFromRegex(document, "age limit")
        val ageLimit = ageLimitTable?.select("li")?.toList()
        if (ageLimit.isNullOrEmpty()) return null
        val calculatedOn = try {
            ageLimitTable.select("h3").text().split("as on")[1].trim()
        } catch (e: Exception) {
            null
        }
        val header = if (calculatedOn != null) listOf("Calculated on", calculatedOn) else emptyList()
        val body = ageLimit.map {
            it.text().split(":").map { text -> text.trim() }
        }
        return Details(header = header, body = body)
    }

    override fun getVacancyDetails(document: Document): Details? {
        return createDetails(document, "vacancy detail", false)
    }

    override fun getFeeDetails(document: Document): Details? {
        return createDetailsFromList(document, "application fee")
    }

    override fun getDates(document: Document): Details? {
        return createDetailsFromList(document, "important date")
    }

    override fun getBasicDetails(document: Document): BasicDetails? {
        val companyName = findMainTable(document).select("tr").toList().first()
            .select("h2").text().trim()
        val name = document.select("h1").text().trim()
        return BasicDetails(name = name, formTye = FormType.ONLINE, company = companyName)
    }

    private fun createDetailsFromList(document: Document, regexPattern: String): Details? {
        val details = findDataFromRegex(document, regexPattern)
            ?.select("li")
            ?.toList()
        if (details.isNullOrEmpty()) return null
        val body = details.map {
            it.text().split(":").map { text -> text.trim() }
        }
        return Details(body = body)
    }

    private fun findDataFromRegex(document: Document, regexPattern: String): Element? {
        return findMainTable(document)
            .select("td").toList().find { element ->
                listOf("h2", "h3", "h4").any {
                    element.select(it).text().contains(Regex(regexPattern, RegexOption.IGNORE_CASE))
                }
            }
    }

    private fun findMainTable(document: Document): Element {
        return document.select("table").toList().last()
    }

    private fun getTableOfMultipleRows(document: Document, regexPattern: String): List<Element> {
        val tableData = findMainTable(document).select("tr")
            ?.toList() ?: emptyList()
        val startIndex = tableData.indexOfFirst { element ->
            listOf("h2", "h3", "h4").any {
                element.select(it).text().contains(Regex(regexPattern, RegexOption.IGNORE_CASE))
            }
        }

        val endIndex = tableData.subList(startIndex + 1, tableData.size)
            .indexOfFirst { element ->
                listOf("h2", "h3", "h4").any {
                    element.select(it).text() != ""
                }
            } + startIndex + 1
        return tableData.subList(startIndex + 1, endIndex)
    }

    private fun createDetails(document: Document, regexPattern: String, skipHeader: Boolean = false): Details {
        val liElements = findDataFromRegex(document, regexPattern)?.select("li")
        return if (liElements?.text() != "") {
            val body = liElements?.toList()
                ?.map {
                    it.text().split(":").map { it.trim() }
                }
            Details(body = body!!)
        } else {
            val body = getTableOfMultipleRows(document, regexPattern)
                .map {
                    it.select("td")
                        .toList()
                        .map { element -> element.text().trim() }
                }
            if (skipHeader)
                Details(body = body)
            else
                Details(header = body.first(), body = body.subList(1, body.size))
        }
    }
}
