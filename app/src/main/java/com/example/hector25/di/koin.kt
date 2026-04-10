package com.example.hector25.di

import com.example.hector25.data.KtorSimplyRetsAPI
import com.example.hector25.data.PropertyRepository
import com.example.hector25.data.SimplyRetsAPI
import com.example.hector25.user_interface.dashBoard.DashboardViewModel
import com.example.hector25.user_interface.property.PropertyDetailViewModel
import com.example.hector25.user_interface.search.SearchViewModel
import io.ktor.client.HttpClient
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.context.startKoin
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val dataModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true }, contentType = ContentType.Any)
            }
            install(HttpTimeout) {
                requestTimeoutMillis = 60000
                connectTimeoutMillis = 30000
                socketTimeoutMillis = 60000
            }
        }
    }

    single<SimplyRetsAPI> { KtorSimplyRetsAPI(get()) }
    single<PropertyRepository> { PropertyRepository(get()) }
}

val viewModelModule = module {
    factoryOf(::DashboardViewModel)
    factoryOf(::SearchViewModel)
    factoryOf(::PropertyDetailViewModel)
}

fun initKoin() {
    startKoin {
        modules(dataModule, viewModelModule)
    }
}