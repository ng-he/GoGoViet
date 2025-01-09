package com.example.gogoviet.data.models

import kotlinx.serialization.Serializable

@Serializable
data class PlacesRichData(
    val description: String? = null,
    val photos: List<Photo> = emptyList()
)

@Serializable
data class Photo(
    val id: String,
    val prefix: String,
    val suffix: String,
)

@Serializable
data class Review(val username: String = "", val comment: String = "", val rating: Int = 0)