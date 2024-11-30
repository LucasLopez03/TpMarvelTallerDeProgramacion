package com.unlam.tpmarvel.model

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable

interface MarvelApi {
    val httpClient: HttpClient

    suspend fun getAllCharacters(timestamp: Long, md5: String): List<CharacterResult>
    suspend fun searchCharacterByName(name: String): List<CharacterResult>
}

open class MarvelClient(
    private val apiKey: String,
    private val privateKey: String,
    private val json: Json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
        isLenient = true
        prettyPrint = false
        encodeDefaults = true
    },
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO,
    override val httpClient: HttpClient
) : MarvelApi {

    override suspend fun getAllCharacters(timestamp: Long, md5: String): List<CharacterResult> {
        return withContext(dispatcher) {
            val response: MarvelResponse<CharacterResult> = httpClient.get("https://gateway.marvel.com/v1/public/characters") {
                parameter("ts", timestamp)
                parameter("hash", md5)
                parameter("apikey", apiKey)
                parameter("limit", 20)
            }.body()

            response.data.results
        }
    }

    override suspend fun searchCharacterByName(name: String): List<CharacterResult> {
        return withContext(dispatcher) {
            val response: MarvelResponse<CharacterResult> = httpClient.get("https://gateway.marvel.com/v1/public/characters") {
                parameter("nameStartsWith", name)
                parameter("limit", 20)
                parameter("orderBy", "name")
            }.body()

            response.data.results
        }
    }
}

@Serializable
data class MarvelResponse<T>(
    @SerialName("code") val code: Int,
    @SerialName("status") val status: String,
    @SerialName("data") val data: MarvelData<T>
)

@Serializable
data class MarvelData<T>(
    @SerialName("offset") val offset: Int,
    @SerialName("limit") val limit: Int,
    @SerialName("total") val total: Int,
    @SerialName("count") val count: Int,
    @SerialName("results") val results: List<T>
)

@Serializable
data class CharacterResult(
    @SerialName("id") val id: Long,
    @SerialName("name") val name: String,
    @SerialName("description") val description: String,
    @SerialName("thumbnail") val thumbnail: Thumbnail,
    @SerialName("comics") val comics: ResourceList = ResourceList(),
    @SerialName("series") val series: ResourceList = ResourceList(),
    @SerialName("stories") val stories: ResourceList = ResourceList(),
    @SerialName("events") val events: ResourceList = ResourceList(),
    @SerialName("urls") val urls: List<MarvelUrl> = emptyList(),
    @SerialName("movies") val movies: ResourceList = ResourceList()
)

@Serializable
data class Thumbnail(
    @SerialName("path") val path: String,
    @SerialName("extension") val extension: String
) {
    fun toUrl(): String {
        return "$path.$extension"
    }
}

@Serializable
data class ResourceList(
    @SerialName("available") val available: Int = 0,
    @SerialName("collectionURI") val collectionURI: String = "",
    @SerialName("items") val items: List<ResourceSummary> = emptyList(),
    @SerialName("returned") val returned: Int = 0
)

@Serializable
data class ResourceSummary(
    @SerialName("resourceURI") val resourceURI: String = "",
    @SerialName("name") val name: String = "",
    @SerialName("type") val type: String = ""
)

@Serializable
data class MarvelUrl(
    @SerialName("type") val type: String,
    @SerialName("url") val url: String
)