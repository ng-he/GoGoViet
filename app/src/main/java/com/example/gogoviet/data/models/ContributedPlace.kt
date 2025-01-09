package com.example.gogoviet.data.models

import android.location.Address
import kotlinx.serialization.Serializable

@Serializable
data class ContributedPlace (
    var id: String = "",
    val name: String = "",
    val user: String = "",
    val description: String = "",
    val category: String = "",
    val address: String = "",
    val location: Coordinates = Coordinates(0.0, 0.0),
    val photos: List<String> = emptyList(),
    val reviews: List<Review> = emptyList()
)