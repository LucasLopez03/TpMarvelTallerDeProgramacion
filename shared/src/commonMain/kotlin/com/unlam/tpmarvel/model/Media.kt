package com.unlam.tpmarvel.model

import kotlinx.serialization.Serializable

@Serializable
data class Media(
    val title: String,
    val imageUrl: String
)