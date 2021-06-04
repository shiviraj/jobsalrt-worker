package com.jobsalrt.worker.service.jobSarkari

import com.jobsalrt.worker.domain.BasicDetails
import com.jobsalrt.worker.domain.Details
import com.jobsalrt.worker.domain.FormType
import com.jobsalrt.worker.service.JobUrlService
import com.jobsalrt.worker.service.postService.PostFetcher
import com.jobsalrt.worker.service.postService.PostService
import com.jobsalrt.worker.service.postService.RawPostService
import com.jobsalrt.worker.webClient.WebClientWrapper
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class JobSarkariPostFetcher(
    @Autowired webClientWrapper: WebClientWrapper,
    @Autowired postService: PostService,
    @Autowired rawPostService: RawPostService,
    @Autowired jobUrlService: JobUrlService
) :
    PostFetcher(webClientWrapper, postService, rawPostService, jobUrlService) {
    override fun parseHtml(document: Document): String {
        return document.select(".job_card").toString()
    }

    override fun getOtherDetails(document: Document): Map<String, Details> {
        val regexPattern =
            "(important date|application fee|vacancy details|age limit|selection process|how to apply|important link)"

        val map = mutableMapOf<String, Details>()
        document.select(".job_card").filterNot {
            it.select("h2").text().contains(Regex(regexPattern, RegexOption.IGNORE_CASE))
        }
            .toList()
            .map {
                val tableName = it.select("h2").text().trim().replace(".", "")
                val table = it.select(".table")
                val pair = getHeadAndBodyFromTable(table)
                if (pair != null && !pair.second.isNullOrEmpty()) {
                    map[tableName] = Details(pair.first, body = pair.second)
                }
            }
        return map
    }

    override fun getImportantLinks(document: Document): Details? {
        val table = document.select(".job_card").find {
            it.select("h2").text().contains(Regex("important link", RegexOption.IGNORE_CASE))
        } ?: return null

        val links = table.select("tr").toList()

        if (links.isNullOrEmpty()) return null

        val endIndex = links.indexOfFirst {
            it.select("td").text().contains(Regex("official website", RegexOption.IGNORE_CASE))
        }.plus(1)

        return parseImportantLinks(links.subList(0, endIndex))
    }

    override fun getHowToApplyDetails(document: Document): List<String>? {
        val pair = findTableAndSelectHeaderAndBody(document, "how to apply") ?: return null
        return pair.second.flatten()
    }

    override fun getSelectionProcessDetails(document: Document): List<String>? {
        val pair = findTableAndSelectHeaderAndBody(document, "selection process") ?: return null
        return pair.second.flatten()
    }

    override fun getAgeLimitDetails(document: Document): Details? {
        val pair = findTableAndSelectHeaderAndBody(document, "age limit") ?: return null
        return Details(pair.first, body = pair.second)
    }

    override fun getVacancyDetails(document: Document): Details? {
        val pair = findTableAndSelectHeaderAndBody(document, "vacancy details") ?: return null
        return Details(pair.first, body = pair.second)
    }

    override fun getFeeDetails(document: Document): Details? {
        val pair = findTableAndSelectHeaderAndBody(document, "application fee") ?: return null
        return Details(pair.first, body = pair.second)
    }

    override fun getDates(document: Document): Details? {
        val pair = findTableAndSelectHeaderAndBody(document, "important date") ?: return null
        return Details(pair.first, body = pair.second)
    }

    override fun getBasicDetails(document: Document): BasicDetails {
        val advtNo = getDates(document)?.body
            ?.find {
                it.first().contains(Regex("advt", RegexOption.IGNORE_CASE))
            }?.get(1)

        val map = createBasicDetailsMap(document)
        return BasicDetails(
            name = document.select("h1").text().trim(),
            formType = FormType.of(findValueFromKeyRegex(map, "form type") ?: ""),
            advtNo = advtNo,
            lastDate = try {
                LocalDate.parse(findValueFromKeyRegex(map, "last date"), DateTimeFormatter.ofPattern("dd/MM/yyyy"))
            } catch (e: Exception) {
                null
            },
            totalVacancies = try {
                findValueFromKeyRegex(map, "total vacancies")?.toLong()
            } catch (e: Exception) {
                null
            },
            location = findValueFromKeyRegex(map, "location"),
            company = findValueFromKeyRegex(map, "company"),
            qualification = findValueFromKeyRegex(map, "qualification"),
        )
    }

    private fun findTableAndSelectHeaderAndBody(
        document: Document,
        pattern: String
    ): Pair<List<String>, List<List<String>>>? {
        val table = document.select(".job_card")
            .find {
                it.select("h2").text().contains(Regex(pattern, RegexOption.IGNORE_CASE))
            } ?: return null
        return getHeadAndBodyFromTable(table.select(".table"))
    }

    private fun getHeadAndBodyFromTable(table: Elements): Pair<List<String>, List<List<String>>>? {
        val header = table.select("thead th")
            .toList()
            .map {
                it.text().trim()
            }

        val body = table.select("tr")
            .toList()
            .map {
                it.select("td")
                    .toList()
                    .map { element ->
                        element.text().trim()
                    }
            }
            .filter {
                it.isNotEmpty()
            }

        if (body.isNullOrEmpty()) return null

        return Pair(header, body)
    }

    private fun createBasicDetailsMap(document: Document): MutableMap<String, String> {
        val map = mutableMapOf<String, String>()
        document.select("#post-title1").select("p")
            .toList()
            .forEach {
                val key = it.select(".option_name_l").text()
                    .split(":")[0]
                    .trim()
                    .toLowerCase().replace(".", "")
                map[key] = it.select(".sec_detail").text().trim()
            }
        return map
    }

    private fun findValueFromKeyRegex(map: Map<String, String>, key: String): String? {
        val keyName = map.keys.find {
            it.contains(Regex(key, RegexOption.IGNORE_CASE))
        }
        return map[keyName]
    }
}
