package com.unlam.tpmarvel.model

import kotlinx.serialization.Serializable

@Serializable
data class Character(
    val id: Long,
    val name: String,
    val description: String,
    val thumbnailUrl: String,
    val movies: List<Media> = emptyList(),
    val series: List<Media> = emptyList()
)