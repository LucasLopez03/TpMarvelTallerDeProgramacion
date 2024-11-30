package com.unlam.tpmarvel.data

import com.unlam.tpmarvel.model.Character

interface CharactersRepository {
    suspend fun getCharacters(timestamp: Long, md5: String): List<Character>
    suspend fun searchCharacterByName(query: String): List<Character>
}