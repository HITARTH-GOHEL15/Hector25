package com.example.hector25.data

import kotlinx.serialization.Serializable

@Serializable
data class SimplyRetsProperty(
    val mlsId: Int? = null,
    val listingId: String? = null,
    val listPrice: Int? = null,
    val property: PropertyDetail? = null,
    val address: PropertyAddressSimply? = null,
    val photos: List<String>? = null,
    val remarks: String? = null,
    val agent: Agent? = null,
    val mls: MlsInfo? = null
)

@Serializable
data class PropertyDetail(
    val bedrooms: Int? = null,
    val bathsFull: Int? = null,
    val bathsHalf: Int? = null,
    val area: Int? = null,         // sqft
    val style: String? = null,
    val type: String? = null,
    val pool: String? = null,
    val stories: Int? = null
)

@Serializable
data class PropertyAddressSimply(
    val full: String? = null,
    val city: String? = null,
    val state: String? = null,
    val postalCode: String? = null,
    val streetName: String? = null,
    val streetNumber: Int? = null
)

@Serializable
data class Agent(
    val firstName: String? = null,
    val lastName: String? = null,
    val id: String? = null
)

@Serializable
data class MlsInfo(
    val status: String? = null,
    val daysOnMarket: Int? = null,
    val area: String? = null
)