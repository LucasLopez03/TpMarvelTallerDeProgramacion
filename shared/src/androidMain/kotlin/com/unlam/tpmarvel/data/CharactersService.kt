package com.unlam.tpmarvel.data

import com.unlam.tpmarvel.model.Character
import com.unlam.tpmarvel.utils.PRIVATE_KEY
import com.unlam.tpmarvel.utils.PUBLIC_KEY
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

class CharactersService(
    private val charactersRepository: CharactersRepository,
    private val errorHandler: ErrorHandler = DefaultErrorHandler()
) {
    suspend fun getCharacters(): List<Character> = try {
        val timestamp = System.currentTimeMillis()
        charactersRepository.getCharacters(
            timestamp = timestamp,
            md5 = generateMd5Hash("$timestamp$PRIVATE_KEY$PUBLIC_KEY")
        )
    } catch (e: Exception) {
        errorHandler.handleError(e)
        emptyList()
    }

    suspend fun searchCharacter(query: String): List<Character> = try {
        charactersRepository.searchCharacterByName(query)
    } catch (e: Exception) {
        errorHandler.handleError(e)
        emptyList()
    }

    private fun generateMd5Hash(input: String): String {
        return try {
            val md = MessageDigest.getInstance("MD5")
            val digest = md.digest(input.toByteArray())
            digest.joinToString("") { "%02x".format(it) }
        } catch (e: NoSuchAlgorithmException) {
            errorHandler.handleError(e)
            throw IllegalStateException("MD5 algorithm not available", e)
        }
    }

    private fun sort(characters: List<Character>): List<Character> =
        characters.sortedWith(CharacterComparator())

    private class CharacterComparator : Comparator<Character> {
        override fun compare(c1: Character, c2: Character): Int = when {
            c1.description.isEmpty() && c2.description.isEmpty() -> c2.id.compareTo(c1.id)
            c1.description.isNotEmpty() && c2.description.isEmpty() -> -1
            c1.description.isEmpty() && c2.description.isNotEmpty() -> 1
            else -> c1.id.compareTo(c2.id)
        }
    }
}

interface ErrorHandler {
    fun handleError(error: Exception)
}

class DefaultErrorHandler : ErrorHandler {
    override fun handleError(error: Exception) {
        // Implementar logging apropiado aquí
        error.printStackTrace()
    }
}