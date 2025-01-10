package com.example.gogoviet

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.gogoviet.data.models.Coordinates
import com.example.gogoviet.ui.theme.Teal1
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.serialization.Serializable

@Serializable
data class SavedPlaces(
    val name: String = "",
    val address: String = "",
    val location: Coordinates = Coordinates(0.0,0.0)
)

var savedClickedPlaceLocation: LatLng? = null

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SavedScreen(authViewModel: AuthViewModel, context: Context, navController: NavHostController) {
    var searchQuery by remember { mutableStateOf("") }
    var isSearchBarActive by remember { mutableStateOf(false) }
    var savedPlaces by remember { mutableStateOf(emptyList<SavedPlaces>()) }
    var searchedSavedPlaces by remember { mutableStateOf(emptyList<Pair<SavedPlaces, Int>>()) }

    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        val userCollection = db.collection("users")

        userCollection.document(authViewModel.userInfo.value!!.uid).get().addOnSuccessListener {
            if(it.get("saved_places") != null) {
                var results = it.get("saved_places") as List<HashMap<String, Any>>
                savedPlaces = results.map { result ->
                    val coordinatesMap = result["location"] as HashMap<String, Double>

                    SavedPlaces(
                        name = result["name"].toString(),
                        address = result["address"].toString(),
                        location = Coordinates(
                            latitude = coordinatesMap["latitude"] ?: 0.0,
                            longitude = coordinatesMap["longitude"] ?: 0.0
                        )
                    )
                }
            }
        }
    }

    LaunchedEffect(savedPlaces) {
        var i = 0
        searchedSavedPlaces = savedPlaces.map {
            println(i)
            Pair(it, i++)
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { isTraversalGroup = true }
        ){
            Surface (color = Teal1) {
                SearchBar(
                    query = searchQuery,
                    onQueryChange = { str ->
                        run {
                            searchQuery = str
                            searchedSavedPlaces = searchSavedPlace(str, savedPlaces)
                        }
                    },
                    onSearch = { str ->
                        run {
                            isSearchBarActive = false
                        }
                    },
                    active = false,
                    onActiveChange = { isSearchBarActive = it },
                    placeholder = { Text("Tìm kiếm địa điểm...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search Icon")
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(onClick = { searchQuery = "" }) {
                                Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                            }
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .fillMaxWidth()
                        .padding(start = 10.dp, end = 10.dp, bottom = 10.dp)
                        .semantics { traversalIndex = 0f },

                    windowInsets = WindowInsets(0.dp),
                    colors = SearchBarDefaults.colors(containerColor = Color.White)
                ) {
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(color = Color.White)
        ) {
            for (p in searchedSavedPlaces) {
                SavedListItem(
                    place = p.first, p.second,
                    navController = navController,
                    onDeleteConfirm = {
                        val db = FirebaseFirestore.getInstance()
                        val userCollection = db.collection("users")

                        var updatedSavedPlaces: MutableList<SavedPlaces> = savedPlaces.toMutableList()
                        updatedSavedPlaces.removeAt(p.second)
                        savedPlaces = updatedSavedPlaces

                        userCollection.document(authViewModel.userInfo.value!!.uid)
                            .update("saved_places", updatedSavedPlaces)
                            .addOnSuccessListener {
                                Toast.makeText(context, "Xóa địa điểm thành công!", Toast.LENGTH_LONG).show()
                            }
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SavedListItem(place: SavedPlaces, idx: Int, navController: NavHostController, onDeleteConfirm: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Xóa địa điểm") },
            text = { Text("Xóa địa điểm ${place.name} khỏi danh sách đã lưu?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDialog = false
                        onDeleteConfirm()
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .padding(start = 10.dp, end = 10.dp, top = 10.dp)
            .combinedClickable(
                onClick = {
                    savedClickedPlaceLocation = LatLng(place.location.latitude, place.location.longitude)
                    navController.navigate("explore")
                },
                onLongClick =  {
                    showDialog = true
                }
            )
    ) {
        Column(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .background(color = Color.LightGray, shape = RoundedCornerShape(20))
            .padding(10.dp)
        ) {
            Text(text = place.name, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(5.dp))
            Text(text = place.address)
        }
    }
}

fun searchSavedPlace(query: String, places: List<SavedPlaces>): List<Pair<SavedPlaces, Int>> {
    val tmp: MutableList<Pair<SavedPlaces, Int>> = mutableListOf()
    for(i in places.indices) {
        if(places[i].name.contains(query, ignoreCase = true)) {
            tmp.add(Pair(places[i], i))
        }
    }

    return tmp.toList()
}