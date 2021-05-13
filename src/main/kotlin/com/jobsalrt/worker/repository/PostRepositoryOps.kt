package com.jobsalrt.worker.repository

import com.jobsalrt.worker.controller.view.FilterRequest
import com.jobsalrt.worker.domain.Post
import org.bson.Document
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.data.mongodb.core.query.Criteria
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.Update
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import kotlin.math.ceil

@Service
class PostRepositoryOps(
    @Autowired val mongoOperations: ReactiveMongoOperations
) {
    private val limit = 48
    fun findPosts(filterRequest: FilterRequest, page: Int): Flux<Post> {
        val query = createQueryWithFilter(filterRequest)
            .skip(((page - 1) * limit).toLong())
            .limit(limit)
        val fields = listOf("basicDetails", "source", "createdAt", "status", "postUpdateDate", "source", "totalViews")
        fields.forEach { query.fields().include(it) }
        return mongoOperations.find(query, Post::class.java)
    }

    fun findPostCount(filterRequest: FilterRequest): Mono<Pair<Long, Double>> {
        return mongoOperations.count(createQueryWithFilter(filterRequest), Post::class.java)
            .map { Pair(it, ceil(it.toDouble() / limit)) }
    }

    private fun createQueryWithFilter(filterRequest: FilterRequest): Query {
        val query = Query()
        if (filterRequest.status.isNotEmpty()) query.addCriteria(Criteria.where("status").`in`(filterRequest.status))
        if (filterRequest.formType.isNotEmpty()) query.addCriteria(
            Criteria.where("basicDetails.formType").`in`(filterRequest.formType)
        )
        if (filterRequest.type.isNotEmpty()) query.addCriteria(Criteria.where("states.type").`in`(filterRequest.type))
        return query
    }
}