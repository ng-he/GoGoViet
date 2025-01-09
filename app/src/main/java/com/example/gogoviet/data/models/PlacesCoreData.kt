package com.example.gogoviet.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.util.Objects

@Serializable
data class PlacesCoreData (
    @SerialName("fsq_id") val fsqId: String? = null,          // Unique identifier for the place
    var name: String? = null,          // Best known name for the place
    val geocodes: Geocodes? = null,    // Geocodes associated with the place
    val location: Location? = null,    // Location details of the place
    val categories: List<Category> = emptyList(), // Categories associated with the place
    val chains: List<Chain> = emptyList(),        // Chains associated with the place
    @SerialName("related_places") val relatedPlaces: RelatedPlaces? = null,           // Related places (parent/children)
    val timezone: String? = null,      // Timezone of the place
    val distance: Int? = null,         // Distance from the provided location (meters)
    val link: String? = null,          // URL for the place details
    @SerialName("closed_bucket") val closedBucket: String? = null   // Open/closed probability status
)

@Serializable
data class Geocodes(
    val main: Coordinates,    // Primary coordinates
    val roof: Coordinates? = null,    // Roof coordinates (if available)
    @SerialName("drop_off") val dropOff: Coordinates? = null, // Drop-off coordinates (if available)
    @SerialName("front_door") val frontDoor: Coordinates? = null, // Front-door coordinates (if available)
    val road: Coordinates? = null     // Road coordinates (if available)
)

@Serializable
data class Coordinates(
    val latitude: Double = 0.0,
    val longitude: Double = 0.0
)

@Serializable
data class Location(
    val address: String? = null,
    @SerialName("address_extended") val addressExtended: String? = null,
    val locality: String? = null,
    val dma: String? = null,
    val region: String? = null,
    val postcode: String? = null,
    val country: String? = null,
    @SerialName("admin_region") val adminRegion: String? = null,
    @SerialName("post_town") val postTown: String? = null,
    @SerialName("po_box") val poBox: String? = null,
    @SerialName("cross_street") val crossStreet: String? = null,
    @SerialName("formatted_address") val formattedAddress: String? = null,
    @SerialName("census_block") val censusBlock: String? = null // US-specific field
)

@Serializable
data class Category(
    val id: Int,       // Category ID
    val name: String,     // Category label
    val icon: CategoryIcon,     // Icon for the category
)

@Serializable
data class Chain(
    val id: String,       // Chain ID
    val name: String      // Chain name
)

@Serializable
data class RelatedPlaces(
    val parent: PlacesCoreData? = null,     // Parent place
    val children: List<PlacesCoreData>? = emptyList() // Children places
)

@Serializable
data class CategoryIcon (
    val prefix: String,
    val suffix: String
)
