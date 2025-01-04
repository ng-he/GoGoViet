package com.example.gogoviet

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.location.Address
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
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
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.ChipColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonElevation
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.currentCompositionLocalContext
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
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
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import com.example.gogoviet.ui.theme.Teal1
import com.google.android.gms.common.internal.safeparcel.SafeParcelable
import com.google.android.gms.maps.CameraUpdate
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.CameraPositionState
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.Json

var places = loadPlacesFromRaw()

@Composable
fun ExploreScreen(context: Context) {
    MapScreen(context)
}

fun markerOf(placeType: String) : Int {
    return when(placeType) {
        "history" -> R.drawable.ic_history_map_marker
        "restaurant" -> R.drawable.ic_restaurant_map_marker
        "park" -> R.drawable.ic_park_map_marker
        "landscapes" -> R.drawable.ic_landscapes_map_marker
        else -> 0
    }
}

@Composable
fun FilterChips(selectedType: String, onTypeSelected: (String) -> Unit) {
    val placeTypes = listOf("history", "restaurant", "park", "landscapes")

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
        "landscapes" -> "Danh lang thắng cảnh"
        else -> ""
    }
}

fun searchLocation(query: String, selectedType: String): MutableList<Place> {
    val searchedPlaces = mutableListOf<Place>()

    places.forEach {
        place -> run {
            if(place.name.startsWith(query, ignoreCase = true) ||
                placeTypeName(place.category).startsWith(selectedType, ignoreCase = true)) {
                searchedPlaces.add(place)
            }
        }
    }

    return searchedPlaces
}

@SuppressLint("DiscouragedApi")
@Composable
fun BottomSheetContent(place: Place, context: Context) {
    var showDialog by remember { mutableStateOf(false) }
    var selectedStars by remember { mutableIntStateOf(0) }
    var comment by remember { mutableStateOf("") }
    var showShareDialog by remember { mutableStateOf(false) }

    Column(modifier = Modifier
        .padding(16.dp)
        .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = place.name,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
            text = place.address.ifEmpty { "Chưa được cung cấp địa chỉ" },
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Row(modifier = Modifier
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Chip(
                onClick = {
                    Toast.makeText(context, "Đã lưu địa điểm", Toast.LENGTH_LONG).show()
                },
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
                onClick = { showShareDialog = true },
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

        if (showShareDialog) {
            ShareDialog(
                message = """
                    Hãy xem địa điểm tuyệt vời này!
                    - ${place.name}
                    - ${place.address}
                    """.trimIndent(),
                onDismiss = { showShareDialog = false }
            )
        }

        if(place.images != null) {
            LazyRow(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(place.images) { image ->
                    Image(
                        painter = painterResource(
                            id = LocalContext.current.resources.getIdentifier(image, "drawable", LocalContext.current.packageName)
                        ),
                        contentDescription = place.name,
                        modifier = Modifier
                            .clip(
                                shape = RoundedCornerShape(
                                    topStart = 16.dp,
                                    topEnd = 16.dp,
                                    bottomStart = 16.dp,
                                    bottomEnd = 16.dp
                                )
                            )
                            .fillMaxSize()
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

        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            Chip(
                onClick = { showDialog = true },
                label = {
                    Text(text = "Viết bài đánh giá")
                },
                modifier = Modifier
                    .height(40.dp)
                    .padding(horizontal = 4.dp)
                    .align(alignment = Alignment.Center),
                colors = ChipDefaults.chipColors(
                    backgroundColor = Color(0x337F97FF),
                    contentColor = Color.White
                ),
                icon = {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.ic_comment),
                        contentDescription = ""
                    )
                }
            )

            CommentDialog(
                showDialog = showDialog,
                onDismiss = { showDialog = false },
                onSubmit = { stars, commentText ->
                    selectedStars = stars
                    comment = commentText

                    val review = Review("username111", comment, stars)
                    place.reviews?.add(0, review)

                    println("Stars: $stars, Comment: $commentText")
                }
            )
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

@SuppressLint("DiscouragedApi")
@Composable
fun SuggestionItem(suggestion: Suggestion) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        val context = LocalContext.current

        Image(
            painter = painterResource(id = context.resources.getIdentifier(suggestion.icon, "drawable", context.packageName)),
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

@SuppressLint("MutableCollectionMutableState")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(context: Context) {
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
            BottomSheetContent(clickedPlace, context)
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    var searchedPlaces by remember { mutableStateOf(places) }
    var isSearchBarActive by remember { mutableStateOf(false) }

    var selectedType by remember { mutableStateOf("") }

    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
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
                            searchedPlaces = searchLocation(searchQuery, searchQuery)
                        } },
                        onSearch = {
                                str -> run {
                            isSearchBarActive = false
                            searchedPlaces = searchLocation(searchQuery, selectedType)
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

                    searchedPlaces = searchLocation(searchQuery, selectedType)
                }
            )

            Box(modifier = Modifier.fillMaxSize()) {
                FloatingActionButton(
                    onClick = {
                        val intent = Intent(context, ContributeActivity::class.java)
                        context.startActivity(intent)
                    },
                    shape = RoundedCornerShape(16.dp),
                    contentColor = Color(0xFF49C2E9),
                    containerColor = Color.White,
                    modifier = Modifier
                        .padding(8.dp)
                        .height(40.dp)
                        .width(110.dp)
                        .align(Alignment.BottomStart)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(imageVector = ImageVector.vectorResource(id = R.drawable.ic_add_outlined), contentDescription = "")
                        Text(text = "Đóng góp")
                    }

                }
            }
        }
    }
}

@SuppressLint("AutoboxingStateCreation")
@Composable
fun CommentDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onSubmit: (Int, String) -> Unit
) {
    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Surface(
                shape = MaterialTheme.shapes.medium,
                color = MaterialTheme.colorScheme.background,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Viết bài đánh giá",
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Star Rating
                    var selectedStars by remember { mutableIntStateOf(0) }
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        for (i in 1..5) {
                            IconButton(
                                onClick = { selectedStars = i },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(
                                    imageVector = if (i <= selectedStars) Icons.Default.Star else Icons.Outlined.Star,
                                    contentDescription = "Star $i"
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Comment Field
                    var comment by remember { mutableStateOf("") }
                    OutlinedTextField(
                        value = comment,
                        onValueChange = { comment = it },
                        label = { Text("Viết bình luận") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Buttons
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = onDismiss,
                            border = BorderStroke(1.dp, color = Color(0xFF49C2E9)),
                            colors = ButtonDefaults.outlinedButtonColors().copy(contentColor = Color(0xFF49C2E9), containerColor = Color.White)) {
                            Text("Hủy")
                        }
                        Button(
                            onClick = {
                                onSubmit(selectedStars, comment)
                                onDismiss()
                            },
                            colors = ButtonDefaults.buttonColors().copy(containerColor = Color(0xFF49C2E9))
                        ) {
                            Text("Đăng")
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ShareDialog(
    message: String,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    // Activity launcher for sharing
    val shareLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult(),
        onResult = { onDismiss() } // Close dialog after sharing
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
        title = { Text(text = "Chia sẻ địa điểm") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Add share options (e.g., Messenger, Email, etc.)
                ShareOption("Messenger") {
                    val messengerIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        `package` = "com.facebook.orca" // Messenger package name
                        putExtra(Intent.EXTRA_TEXT, message)
                    }
                    shareLauncher.launch(Intent.createChooser(messengerIntent, "Share via Messenger"))
                }
                ShareOption("Các app khác") {
                    val generalShareIntent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_TEXT, message)
                    }
                    shareLauncher.launch(Intent.createChooser(generalShareIntent, "Share via"))
                }
            }
        }
    )
}

@Composable
fun ShareOption(label: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        ClickableText(
            text = AnnotatedString(label),
            onClick = { onClick() },
            modifier = Modifier.padding(8.dp)
        )
    }
}

@Serializable
data class Review(val name: String, val comment: String, val rating: Int)
@Serializable
data class Suggestion(val title: String, val description: String, val icon: String)
@Serializable
data class Place(
    var name: String = "",
    @Serializable(with = LatLngSerializer::class) var latLng: LatLng = LatLng(0.0, 0.0),
    val reviews: MutableList<Review>? = null,
    val suggestions: MutableList<Suggestion>? = null,
    val images: MutableList<String>? = null,
    var category: String = "",
    var description: String = "",
    var address: String = ""
)

// Custom serializer for Google Maps LatLng
@Serializer(forClass = LatLng::class)
object LatLngSerializer : KSerializer<LatLng> {
    override val descriptor: SerialDescriptor =
        buildClassSerialDescriptor("LatLng") {
            element<Double>("latitude")
            element<Double>("longitude")
        }

    override fun serialize(encoder: Encoder, value: LatLng) {
        val compositeEncoder = encoder.beginStructure(descriptor)
        compositeEncoder.encodeDoubleElement(descriptor, 0, value.latitude)
        compositeEncoder.encodeDoubleElement(descriptor, 1, value.longitude)
        compositeEncoder.endStructure(descriptor)
    }

    override fun deserialize(decoder: Decoder): LatLng {
        val compositeDecoder = decoder.beginStructure(descriptor)
        var latitude = 0.0
        var longitude = 0.0
        loop@ while (true) {
            when (val index = compositeDecoder.decodeElementIndex(descriptor)) {
                0 -> latitude = compositeDecoder.decodeDoubleElement(descriptor, 0)
                1 -> longitude = compositeDecoder.decodeDoubleElement(descriptor, 1)
                CompositeDecoder.DECODE_DONE -> break@loop
                else -> throw SerializationException("Unexpected index: $index")
            }
        }
        compositeDecoder.endStructure(descriptor)
        return LatLng(latitude, longitude)
    }
}

fun loadPlacesFromRaw(): MutableList<Place> {
    return try {
        // Read JSON file as a string
        val inputStream = MainActivity.getAppResources().openRawResource(R.raw.places)
        val json = inputStream.bufferedReader(Charsets.UTF_8).use { it.readText() }

        println(json)

        // Deserialize JSON into a list of Place objects
        Json.decodeFromString<MutableList<Place>>(json)
    } catch (e: Exception) {
        Log.e("LoadPlaces", "Error loading places: ${e.message}")
        mutableListOf<Place>()
    }
}