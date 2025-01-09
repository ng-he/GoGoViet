package com.example.gogoviet.data

import com.example.gogoviet.data.models.PlacesCoreData
import com.google.android.gms.maps.model.LatLng
import okhttp3.OkHttpClient
import okhttp3.Request
import com.example.gogoviet.BuildConfig
import com.example.gogoviet.data.models.PlacesRichData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

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
    }
}