package com.example.hector25.data

class PropertyRepository(
    private val api: SimplyRetsAPI
) {
    suspend fun getAllProperties(limit: Int = 20): List<SimplyRetsProperty> {
        return api.getProperties(limit = limit)
    }

    suspend fun getBuyProperties(): List<SimplyRetsProperty> {
        return api.getProperties(limit = 10, minPrice = 200000)
    }

    suspend fun getRentProperties(): List<SimplyRetsProperty> {
        // SimplyRETS demo data — filter lower priced for "rent" feel
        return api.getProperties(limit = 10, maxPrice = 500000)
    }

    suspend fun searchProperties(minBeds: Int? = null, minPrice: Int? = null, maxPrice: Int? = null): List<SimplyRetsProperty> {
        return api.getProperties(
            limit = 20,
            minBeds = minBeds,
            minPrice = minPrice,
            maxPrice = maxPrice
        )
    }

    suspend fun getPropertyById(mlsId: Int): SimplyRetsProperty {
        return api.getPropertyById(mlsId)
    }
}