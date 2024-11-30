package com.unlam.tpmarvel.data

import com.unlam.tpmarvel.model.MarvelClient
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.client.plugins.observer.ResponseObserver
import io.ktor.client.request.accept
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import timber.log.Timber

class AndroidMarvelApi(
    apiKey: String,
    privateKey: String
) : MarvelClient(
    apiKey = apiKey,
    privateKey = privateKey,
    dispatcher = Dispatchers.IO,
    httpClient = HttpClient(Android) {
        install(ContentNegotiation) {
            json(Json {
                ignoreUnknownKeys = true
                coerceInputValues = true
                isLenient = true
                encodeDefaults = true
            })
        }

        install(Logging) {
            logger = object : Logger {
                override fun log(message: String) {
                    Timber.tag("MarvelAPI").d(message)
                }
            }
            level = LogLevel.INFO
        }

        install(ResponseObserver) {
            onResponse { response: HttpResponse ->
                Timber.d("HTTP status: ${response.status.value}")
            }
        }

        defaultRequest {
            contentType(ContentType.Application.Json)
            accept(ContentType.Application.Json)
        }

        engine {
            connectTimeout = 30_000
            socketTimeout = 30_000
        }

        expectSuccess = true
    }
)