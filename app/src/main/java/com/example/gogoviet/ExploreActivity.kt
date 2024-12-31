package com.example.gogoviet

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ChipColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.unit.dp
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import com.example.gogoviet.ui.theme.Teal1
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.CameraPositionState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen() {
    val places = listOf(
        Place(
            name = "Văn Miếu Quốc Tử Giám",
            latLng = LatLng(21.028256689682053, 105.83567826735033),
            reviews = listOf(
                Review("Hùng Nguyễn", "Nơi tuyệt vời với các tiện nghi tuyệt vời", 5),
                Review("Tuấn Anh", "Điểm hoàn hảo cho chuyến đi chơi gia đình", 4)
            ),
            suggestions = listOf(
                Suggestion("Địa danh lịch sử", "Các tour du lịch có hướng dẫn có sẵn", R.drawable.ic_castle),
                Suggestion("Vườn quốc gia Ba Vì", "Có rất nhiều loại thực vật", R.drawable.ic_tree)
            ),
            images = listOf("vanmieu"),
            category = "history",
            description = "Văn Miếu – Quốc Tử Giám là quần thể di tích đa dạng, phong phú hàng đầu của thành phố Hà Nội, nằm ở phía Nam kinh thành Thăng Long."
        ),
        Place(
            name = "Nhà Hàng Phong Dê Ninh Bình Cơ Sở 1",
            latLng = LatLng(21.051019152198812, 105.77250583731765),
            reviews = listOf(
                Review("Hùng Nguyễn", "Nơi tuyệt vời với các tiện nghi tuyệt vời", 5),
                Review("Tuấn Anh", "Điểm hoàn hảo cho chuyến đi chơi gia đình", 4)
            ),
            suggestions = listOf(
                Suggestion("Địa danh lịch sử", "Các tour du lịch có hướng dẫn có sẵn", R.drawable.ic_castle),
                Suggestion("Vườn quốc gia Ba Vì", "Có rất nhiều loại thực vật", R.drawable.ic_tree)
            ),
            images = listOf("nhahang"),
            category = "restaurant",
            description = "Phong Dê Ninh Bình - Nhà hàng đặc sản Dê uy tín dành cho bạn!"
        ),
        Place(
            name = "Công viên Thống Nhất",
            latLng = LatLng(21.01716191254088, 105.84438153916992),
            reviews = listOf(
                Review("Hùng Nguyễn", "Nơi tuyệt vời với các tiện nghi tuyệt vời", 5),
                Review("Tuấn Anh", "Điểm hoàn hảo cho chuyến đi chơi gia đình", 4)
            ),
            suggestions = listOf(
                Suggestion("Địa danh lịch sử", "Các tour du lịch có hướng dẫn có sẵn", R.drawable.ic_castle),
                Suggestion("Vườn quốc gia Ba Vì", "Có rất nhiều loại thực vật", R.drawable.ic_tree)
            ),
            images = listOf("congvien"),
            category = "park",
            description = "Công viên thành phố thoáng mát, nhộn nhịp, rợp bóng cây cao, có hồ nước lớn, lối đi tập thể dục và quán cà phê."
        ),
    )

    var clickedPlace by remember { mutableStateOf(Place(latLng = LatLng(21.0278, 105.8342))) }
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(clickedPlace.latLng, 15f)
    }

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    if(showBottomSheet) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                showBottomSheet = false
            },
        ) {
            BottomSheetContent(clickedPlace)
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    var searchedPlaces by remember { mutableStateOf(places) }
    var isSearchBarActive by remember { mutableStateOf(false) }

    var selectedType by remember { mutableStateOf("") }

    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            for(place in searchedPlaces) {
                Marker(
                    state = MarkerState(position = place.latLng),
                    title = place.name,
                    icon = BitmapDescriptorFactory.fromResource(markerOf(place.category)),
                    onClick = {
                        showBottomSheet = true
                        clickedPlace = place
                        true
                    }
                )
            }
        }

        Column(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { isTraversalGroup = true }
            ){
                Surface (color = if(isSearchBarActive) Color.White else Color.Transparent) {
                    SearchBar(
                        query = searchQuery,
                        onQueryChange = {
                            str -> run {
                                searchQuery = str
                                searchedPlaces = searchLocation(searchQuery, searchQuery ,places)
                            } },
                        onSearch = {
                            str -> run {
                                isSearchBarActive = false
                                searchedPlaces = searchLocation(searchQuery, selectedType, places)
                            }
                        },
                        active = isSearchBarActive,
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
                            .padding(start = 10.dp, end = 10.dp)
                            .semantics { traversalIndex = 0f },

                        windowInsets = WindowInsets(0.dp),

                        colors = SearchBarDefaults.colors(containerColor = Color.White)
                    ) {
                        Column {
                            searchedPlaces.forEach {
                                place -> run {
                                    Box(modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable(onClick = {
                                            clickedPlace = place
                                            cameraPositionState.move(
                                                CameraUpdateFactory.newCameraPosition(
                                                    CameraPosition.fromLatLngZoom(
                                                        clickedPlace.latLng,
                                                        15f
                                                    )
                                                )
                                            )
                                            isSearchBarActive = false
                                        })
                                    ) {
                                        Text(text = place.name)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            FilterChips(
                selectedType = selectedType,
                onTypeSelected = {
                    if(selectedType == it) {
                        selectedType = ""
                    }
                    else {
                        selectedType = it
                    }

                    searchedPlaces = searchLocation(searchQuery, selectedType, places)
                }
            )
        }
    }
}

fun markerOf(placeType: String) : Int {
    return when(placeType) {
        "history" -> R.drawable.ic_history_map_marker
        "restaurant" -> R.drawable.ic_restaurant_map_marker
        "park" -> R.drawable.ic_park_map_marker
        else -> 0
    }
}

@Composable
fun FilterChips(selectedType: String, onTypeSelected: (String) -> Unit) {
    val placeTypes = listOf("history", "restaurant", "park")

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        items(placeTypes) { type ->
            Chip(
                onClick = { onTypeSelected(type) },
                label = {
                    Text(text = placeTypeName(type))
                },
                modifier = Modifier
                    .height(40.dp)
                    .padding(horizontal = 4.dp),
                colors = ChipDefaults.chipColors(
                    backgroundColor = Color.White,
                    contentColor = Color.Black
                ),
                border = ChipDefaults.chipBorder(BorderStroke(
                    width = if (type == selectedType) 3.dp else 0.dp,
                    color = Teal1
                ))
            )
        }
    }
}

fun placeTypeName(placeType: String): String {
    return when(placeType) {
        "history" -> "Di tích lịch sử"
        "restaurant" -> "Nhà hàng"
        "park" -> "Công viên"
        else -> ""
    }
}

fun searchLocation(query: String, selectedType: String, places: List<Place>): List<Place> {
    val searchedPlaces = mutableListOf<Place>()

    places.forEach {
        place -> run {
            if(place.name.startsWith(query, ignoreCase = true) &&
                place.category.startsWith(selectedType, ignoreCase = true)) {
                searchedPlaces.add(place)
            }
        }
    }

    return searchedPlaces.toList()
}

@SuppressLint("DiscouragedApi")
@Composable
fun BottomSheetContent(place: Place) {
    Column(modifier = Modifier
        .padding(16.dp)
        .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = place.name,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Row(modifier = Modifier
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Chip(
                onClick = { },
                label = {
                    Text(text = "Lưu")
                },
                modifier = Modifier
                    .height(40.dp)
                    .padding(horizontal = 4.dp),
                colors = ChipDefaults.chipColors(
                    backgroundColor = Color(0x337F97FF),
                    contentColor = Color.White
                ),
                icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_save),
                        contentDescription = ""
                    )
                }
            )

            Chip(
                onClick = { },
                label = {
                    Text(text = "Chia sẻ")
                },
                modifier = Modifier
                    .height(40.dp)
                    .padding(horizontal = 4.dp),
                colors = ChipDefaults.chipColors(
                    backgroundColor = Color(0x337F97FF),
                    contentColor = Color.White
                ),
                icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_share),
                        contentDescription = ""
                    )
                }
            )
        }

        if(place.images != null) {
            LazyRow(
                modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(place.images) { image ->
                    Image(
                        painter = painterResource(
                            id = LocalContext.current.resources.getIdentifier(image, "drawable", LocalContext.current.packageName)
                        ),
                        contentDescription = place.name,
                        modifier = Modifier.clip(shape = RoundedCornerShape(
                            topStart = 16.dp,
                            topEnd = 16.dp,
                            bottomStart = 16.dp,
                            bottomEnd = 16.dp)
                        ).fillMaxSize()
                    )
                }
            }
        }

        Text(text = place.description, modifier = Modifier.padding(bottom = 10.dp))
        Text(text = "Đánh giá của khách thăm quan", style = MaterialTheme.typography.titleMedium)
        if(place.reviews != null) {
            LazyRow(
                modifier = Modifier.padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(place.reviews) { review ->
                    ReviewCard(review)
                }
            }
        }
        else {
            Text(text = "Không có bài đánh giá nào")
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
        Text(text = "Gợi ý địa điểm", style = MaterialTheme.typography.titleMedium)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            place.suggestions?.forEach { suggestion ->
                SuggestionItem(suggestion)
            }
        }
    }
}

@Composable
fun SuggestionItem(suggestion: Suggestion) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Image(
            painter = painterResource(id = suggestion.icon),
            contentDescription = suggestion.title,
            modifier = Modifier.size(40.dp)
        )
        Column(modifier = Modifier.padding(start = 8.dp)) {
            Text(text = suggestion.title, style = MaterialTheme.typography.titleMedium)
            Text(text = suggestion.description, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
fun ReviewCard(review: Review) {
    Card(
        modifier = Modifier
            .width(200.dp)
            .height(100.dp)
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = review.name, style = MaterialTheme.typography.titleMedium)
            Text(text = review.comment, maxLines = 2, style = MaterialTheme.typography.bodyMedium)
            RatingBar(rating = review.rating)
        }
    }
}

@Composable
fun RatingBar(rating: Int) {
    Row {
        repeat(rating) {
            Icon(
                imageVector = Icons.Default.Star,
                contentDescription = "Star",
                tint = Color.Yellow
            )
        }
        repeat(5 - rating) {
            Icon(
                imageVector = Icons.Outlined.Star,
                contentDescription = "Empty Star",
                tint = Color.Gray
            )
        }
    }
}


data class Review(val name: String, val comment: String, val rating: Int)
data class Suggestion(val title: String, val description: String, val icon: Int)
data class Place(
    val name: String = "",
    val latLng: LatLng = LatLng(0.0, 0.0),
    val reviews: List<Review>? = null,
    val suggestions: List<Suggestion>? = null,
    val images: List<String>? = null,
    val category: String = "",
    val description: String = "",
)
