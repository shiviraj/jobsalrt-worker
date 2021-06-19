package com.jobsalrt.worker.repository

import com.jobsalrt.worker.controller.view.FilterRequest
import com.jobsalrt.worker.domain.POST_COLLECTION
import com.jobsalrt.worker.domain.Post
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Sort
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import kotlin.math.ceil

@Service
class PostRepositoryOps(
    @Autowired val mongoOperations: ReactiveMongoOperations
) {
    private val limit = 48
    fun findPosts(filter: FilterRequest, page: Int): Flux<Post> {
        val query = createQueryWithFilter(filter)
            .skip(((page - 1) * limit).toLong())
            .limit(limit)
            .with(Sort.by(Sort.Direction.fromString(filter.sortOrder), filter.sortBy))

        val fields = listOf(
            "basicDetails",
            "source",
            "createdAt",
            "status",
            "postUpdateDate",
            "source",
            "totalViews",
            "isUpdateAvailable"
        )
        fields.forEach { query.fields().include(it) }
        return mongoOperations.find(query, Post::class.java, POST_COLLECTION)
    }

    fun findPostCount(filter: FilterRequest): Mono<Pair<Long, Double>> {
        return mongoOperations.count(createQueryWithFilter(filter), Post::class.java, POST_COLLECTION)
            .map { Pair(it, ceil(it.toDouble() / limit)) }
    }

    fun findByBasicDetailsUrl(url: String): Mono<Post> {
        val query = Query(Criteria.where("basicDetails.url").`is`(url))
        return mongoOperations.findOne(query, Post::class.java, POST_COLLECTION)
    }

    private fun createQueryWithFilter(filter: FilterRequest): Query {
        val query = Query()
        filter.filters.forEach {
            query.addCriteria(Criteria.where(findKey(it.key)).`in`(it.value))
        }
        if (filter.search.isNotEmpty()) {
            query.addCriteria(
                Criteria.where("").orOperator(
                    Criteria.where("basicDetails.url").regex(".*${filter.search}.*", "i"),
                    Criteria.where("basicDetails.name").regex(".*${filter.search}.*", "i"),
                    Criteria.where("basicDetails.location").regex(".*${filter.search}.*", "i"),
                    Criteria.where("basicDetails.company").regex(".*${filter.search}.*", "i"),
                    Criteria.where("basicDetails.qualification").regex(".*${filter.search}.*", "i"),
                )
            )
        }
        return query
    }

    private fun findKey(key: String): String {
        val keyMapping = mapOf(
            "type" to "states.type",
            "formType" to "basicDetails.formType",
            "updateAvailable" to "isUpdateAvailable",
            "status" to "status"
        )
        return keyMapping[key] ?: key
    }
}
