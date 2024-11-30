package com.unlam.tpmarvel.android.model

import android.os.Parcelable
import com.unlam.tpmarvel.model.Character
import com.unlam.tpmarvel.model.Media
import kotlinx.parcelize.Parcelize

@Parcelize
data class AndroidCharacter(
    val id: Long,
    val name: String,
    val description: String,
    val thumbnailUrl: String,
    val movies: List<AndroidMedia> = emptyList(),
    val series: List<AndroidMedia> = emptyList()
) : Parcelable {
    companion object {
        fun fromCharacter(character: Character) = AndroidCharacter(
            id = character.id,
            name = character.name,
            description = character.description,
            thumbnailUrl = character.thumbnailUrl,
            movies = character.movies.map { AndroidMedia.fromMedia(it) },
            series = character.series.map { AndroidMedia.fromMedia(it) }
        )
    }

    fun toCharacter() = Character(
        id = id,
        name = name,
        description = description,
        thumbnailUrl = thumbnailUrl,
        movies = movies.map { it.toMedia() },
        series = series.map { it.toMedia() }
    )
}

@Parcelize
data class AndroidMedia(
    val title: String,
    val imageUrl: String
) : Parcelable {
    companion object {
        fun fromMedia(media: Media) = AndroidMedia(
            title = media.title,
            imageUrl = media.imageUrl
        )
    }

    fun toMedia() = Media(
        title = title,
        imageUrl = imageUrl
    )
}