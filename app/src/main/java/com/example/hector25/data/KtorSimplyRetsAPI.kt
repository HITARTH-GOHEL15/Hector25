package com.example.hector25.data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.basicAuth
import io.ktor.client.request.get
import io.ktor.client.request.parameter

interface SimplyRetsAPI {
    suspend fun getProperties(
        limit: Int = 20,
        minPrice: Int? = null,
        maxPrice: Int? = null,
        minBeds: Int? = null,
        type: String? = null,   // e.g. "residential"
        status: String? = null  // e.g. "Active"
    ): List<SimplyRetsProperty>

    suspend fun getPropertyById(mlsId: Int): SimplyRetsProperty
}

// Demo credentials — no signup needed!
private const val USERNAME = "simplyrets"
private const val PASSWORD = "simplyrets"
private const val BASE_URL = "https://api.simplyrets.com"

class KtorSimplyRetsAPI(private val client: HttpClient) : SimplyRetsAPI {

    override suspend fun getProperties(
        limit: Int,
        minPrice: Int?,
        maxPrice: Int?,
        minBeds: Int?,
        type: String?,
        status: String?
    ): List<SimplyRetsProperty> {
        return client.get("$BASE_URL/properties") {
            basicAuth(USERNAME, PASSWORD)
            parameter("limit", limit)
            minPrice?.let { parameter("minprice", it) }
            maxPrice?.let { parameter("maxprice", it) }
            minBeds?.let { parameter("minbeds", it) }
            type?.let { parameter("type", it) }
            status?.let { parameter("status", it) }
        }.body()
    }

    override suspend fun getPropertyById(mlsId: Int): SimplyRetsProperty {
        return client.get("$BASE_URL/properties/$mlsId") {
            basicAuth(USERNAME, PASSWORD)
        }.body()
    }
}