package com.unlam.tpmarvel.data

import com.unlam.tpmarvel.model.CharacterResult

interface CharacterQueries {
    suspend fun selectAllCharacters(): List<CharacterResult>
    suspend fun searchCharactersByName(name: String): List<CharacterResult>
    suspend fun insertCharacter(
        id: Long,
        name: String,
        description: String,
        thumbnailUrl: String,
        movieTitles: List<String>,
        movieImageUrls: List<String>,
        seriesTitles: List<String>,
        seriesImageUrls: List<String>
    )
    suspend fun deleteAllCharacters()
}