package com.unlam.tpmarvel.data

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.unlam.tpmarvel.AppDatabase
import com.unlam.tpmarvel.model.Character
import com.unlam.tpmarvel.model.CharacterResult
import com.unlam.tpmarvel.model.MarvelApi
import com.unlam.tpmarvel.model.Media

class KtorCharactersRepository(
    private val apiClient: MarvelApi,
    private val appDatabase: AppDatabase,
    private val networkChecker: NetworkConnectivityChecker
) : CharactersRepository {
    private val characterQueries = AppDatabaseCharacterQueries(appDatabase)

    override suspend fun getCharacters(timestamp: Long, md5: String): List<Character> {
        return if (networkChecker.isNetworkAvailable()) {
            try {
                // Intentar obtener datos de la API
                val apiCharacters = apiClient.getAllCharacters(timestamp, md5).map { dto ->
                    mapCharacterFromDTO(dto)
                }

                // Actualizar la caché con los nuevos datos
                characterQueries.deleteAllCharacters()
                apiCharacters.forEach { character ->
                    characterQueries.insertCharacter(
                        character.id,
                        character.name,
                        character.description,
                        character.thumbnailUrl,
                        character.movies.map { it.title },
                        character.movies.map { it.imageUrl },
                        character.series.map { it.title },
                        character.series.map { it.imageUrl }
                    )
                }

                apiCharacters
            } catch (e: Exception) {
                // Si falla la API, usar caché
                getCachedCharacters()
            }
        } else {
            // Sin conexión, usar caché
            getCachedCharacters()
        }
    }

    override suspend fun searchCharacterByName(query: String): List<Character> {
        return if (networkChecker.isNetworkAvailable()) {  // Agregamos networkChecker.
            try {
                // Intentar búsqueda en la API
                val apiCharacters = apiClient.searchCharacterByName(query).map { dto ->
                    mapCharacterFromDTO(dto)
                }
                // Actualizar caché con los resultados
                apiCharacters.forEach { character ->
                    characterQueries.insertCharacter(
                        character.id,
                        character.name,
                        character.description,
                        character.thumbnailUrl,
                        character.movies.map { it.title },
                        character.movies.map { it.imageUrl },
                        character.series.map { it.title },
                        character.series.map { it.imageUrl }
                    )
                }

                apiCharacters
            } catch (e: Exception) {
                // Si falla la API, buscar en caché
                searchInCache(query)
            }
        } else {
            // Sin conexión, buscar en caché
            searchInCache(query)
        }
    }

    private suspend fun getCachedCharacters(): List<Character> {
        return characterQueries.selectAllCharacters().map { mapCharacterFromDTO(it) }
    }

    private suspend fun searchInCache(query: String): List<Character> {
        return characterQueries.searchCharactersByName(query).map { mapCharacterFromDTO(it) }
    }

    private fun mapCharacterFromDTO(dto: CharacterResult): Character {
        return Character(
            id = dto.id,
            name = dto.name,
            description = dto.description,
            thumbnailUrl = dto.thumbnail.toUrl(),
            movies = dto.comics.items  // Cambiamos a comics
                .map { Media(title = it.name, imageUrl = dto.thumbnail.toUrl()) },
            series = dto.series.items
                .map { Media(title = it.name, imageUrl = dto.thumbnail.toUrl()) }
        )
    }
}