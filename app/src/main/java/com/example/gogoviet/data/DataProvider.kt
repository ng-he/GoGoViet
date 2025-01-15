package com.example.gogoviet.data

import com.example.gogoviet.data.models.PlacesCoreData
import com.google.android.gms.maps.model.LatLng
import okhttp3.OkHttpClient
import okhttp3.Request
import com.example.gogoviet.BuildConfig
import com.example.gogoviet.data.models.Coordinates
import com.example.gogoviet.data.models.PlacesRichData
import com.example.gogoviet.data.models.RouteData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import java.math.BigDecimal
import java.math.RoundingMode

@Serializable
data class PlacesResponseData(val results: List<PlacesCoreData>)

val jsonParser = Json { ignoreUnknownKeys = true }

class DataProvider {
    companion object {
        fun getPlaces(latLng: LatLng, radius: Int, onResult: (List<PlacesCoreData>) -> Unit) {
            val client = OkHttpClient()

            val request = Request.Builder()
                .url("https://api.foursquare.com/v3/places/search?ll=${latLng.latitude}%2C${latLng.longitude}&radius=${radius}&limit=50")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", BuildConfig.PLACES_API_KEY)
                .build()

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response = withContext(Dispatchers.IO) {
                        client.newCall(request).execute()
                    }

                    if (response.isSuccessful) {
                        val responseData = response.body?.string()
                        println(responseData)
                        val places = responseData?.let { jsonParser.decodeFromString<PlacesResponseData>(it) }?.results ?: emptyList()
                        onResult(places)
                    } else {
                        println("DataProvider: ${response.message}")
                        onResult(emptyList())
                    }
                } catch (e: Exception) {
                    println("DataProvider: ${e.message}")
                    onResult(emptyList())
                }
            }
        }

        fun getPlacesDetail(placeId: String, onResult: (PlacesRichData?) -> Unit) {
            val client = OkHttpClient()

            val request = Request.Builder()
                .url("https://api.foursquare.com/v3/places/${placeId}?fields=description%2Cphotos%2Crating")
                .get()
                .addHeader("accept", "application/json")
                .addHeader("Authorization", BuildConfig.PLACES_API_KEY)
                .build()

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    val response = withContext(Dispatchers.IO) {
                        client.newCall(request).execute()
                    }

                    if (response.isSuccessful) {
                        val responseData = response.body?.string()
                        println(responseData)
                        val places = responseData?.let { jsonParser.decodeFromString<PlacesRichData>(it) }
                        onResult(places)
                    } else {
                        println("DataProvider: ${response.message}")
                        onResult(null)
                    }
                } catch (e: Exception) {
                    println("DataProvider: ${e.message}")
                    onResult(null)
                }
            }
        }

        fun getRoute(start: LatLng, dest: LatLng, onResult: (RouteData) -> Unit) {
            val client = OkHttpClient()

            val request = Request.Builder()
                .url("https://api.openrouteservice.org/v2/directions/driving-car?api_key=${BuildConfig.ROUTE_API_KEY}&" +
                        "start=${BigDecimal(start.longitude).setScale(6, RoundingMode.HALF_UP).toDouble()},${BigDecimal(start.latitude).setScale(6, RoundingMode.HALF_UP).toDouble()}&" +
                        "end=${BigDecimal(dest.longitude).setScale(6, RoundingMode.HALF_UP).toDouble()},${BigDecimal(dest.latitude).setScale(6, RoundingMode.HALF_UP).toDouble()}")
                .get()
                .addHeader("accept", "application/geo+json;charset=UTF-8")
                .addHeader("Authorization", BuildConfig.ROUTE_API_KEY)
                .build()

            CoroutineScope(Dispatchers.Main).launch {
                try {
                    println(request.url.toString())
                    val response = withContext(Dispatchers.IO) {
                        client.newCall(request).execute()
                    }

                    if (response.isSuccessful) {
                        val responseData = response.body?.string()
                        println("Responsedata: $responseData")
                        val routeData = responseData?.let { jsonParser.decodeFromString<RouteData>(it) }
//                        onResult(routeData!!.features[0].geometry.coordinates.map { LatLng(it[1], it[0]) })
                        onResult(routeData!!)
                    } else {
                        println("DataProvider: ${response.message}")
                        onResult(RouteData())
                    }
                } catch (e: Exception) {
                    println("DataProvider: ${e.message}")
                    onResult(RouteData())
                }
            }
        }
    }
}