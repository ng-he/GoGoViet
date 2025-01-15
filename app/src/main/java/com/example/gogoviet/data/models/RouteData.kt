package com.example.gogoviet.data.models

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonNames

@Serializable
data class RouteData(
    val type: String = "",
    val bbox: List<Double> = emptyList(),
    val features: List<Feature> = emptyList(),
)

@Serializable
data class Feature(
    val bbox: List<Double> = emptyList(),
    val type: String = "",
    val properties: FeatureProperties = FeatureProperties(),
    val geometry: Geometry = Geometry()
)

@Serializable
data class FeatureProperties @OptIn(ExperimentalSerializationApi::class) constructor(
    val segments: List<Segment> = emptyList(),
    @JsonNames("way_points") val wayPoints: List<Double> = emptyList(),
    val summary: Summary = Summary()
)

@Serializable
data class Segment (
    val distance: Double = 0.0,
    val duration: Double = 0.0,
    val steps: List<Step> = emptyList()
)

@Serializable
data class Step @OptIn(ExperimentalSerializationApi::class) constructor(
    val type: Int = 0,
    val instruction: String = "",
    val name: String = "",
    @JsonNames("way_points") val wayPoints: List<Double> = emptyList()
)

@Serializable
data class Summary(
    val distance: Double = 0.0,
    val duration: Double = 0.0
)

@Serializable
data class Geometry(
    val type: String = "",
    val coordinates: List<List<Double>> = emptyList()
)