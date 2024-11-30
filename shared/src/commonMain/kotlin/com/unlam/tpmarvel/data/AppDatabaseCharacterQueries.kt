package com.unlam.tpmarvel.data

import com.unlam.tpmarvel.AppDatabase
import com.unlam.tpmarvel.model.CharacterResult
import com.unlam.tpmarvel.model.ResourceList
import com.unlam.tpmarvel.model.ResourceSummary
import com.unlam.tpmarvel.model.Thumbnail

class AppDatabaseCharacterQueries(
    private val appDatabase: AppDatabase
) : CharacterQueries {
    override suspend fun selectAllCharacters(): List<CharacterResult> {
        return appDatabase.appDatabaseQueries.selectAllCharacters().executeAsList().map { row ->
            CharacterResult(
                id = row.id,
                name = row.name,
                description = row.description,
                thumbnail = Thumbnail(
                    path = row.thumbnailUrl,
                    extension = "jpg"
                ),
                series = ResourceList(
                    items = row.seriesTitles.split(",").map { title ->
                        ResourceSummary(name = title)
                    }
                ),
                movies = ResourceList(
                    items = row.movieTitles.split(",").map { title ->
                        ResourceSummary(name = title)
                    }
                )
            )
        }
    }

    override suspend fun searchCharactersByName(name: String): List<CharacterResult> {
        return appDatabase.appDatabaseQueries.searchCharactersByName(name).executeAsList().map { row ->
            CharacterResult(
                id = row.id,
                name = row.name,
                description = row.description,
                thumbnail = Thumbnail(
                    path = row.thumbnailUrl,
                    extension = "jpg"
                ),
                series = ResourceList(
                    items = row.seriesTitles.split(",").map { title ->
                        ResourceSummary(name = title)
                    }
                ),
                movies = ResourceList(
                    items = row.movieTitles.split(",").map { title ->
                        ResourceSummary(name = title)
                    }
                )
            )
        }
    }

    override suspend fun insertCharacter(
        id: Long,
        name: String,
        description: String,
        thumbnailUrl: String,
        movieTitles: List<String>,
        movieImageUrls: List<String>,
        seriesTitles: List<String>,
        seriesImageUrls: List<String>
    ) {
        appDatabase.appDatabaseQueries.insertCharacter(
            id, name, description, thumbnailUrl,
            movieTitles.joinToString(","),
            movieImageUrls.joinToString(","),
            seriesTitles.joinToString(","),
            seriesImageUrls.joinToString(",")
        )
    }

    override suspend fun deleteAllCharacters() {
        appDatabase.appDatabaseQueries.deleteAllCharacters()
    }
}