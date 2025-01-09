package com.example.gogoviet

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Paint
import android.graphics.Path
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.app.ActivityCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.wear.compose.material.Chip
import androidx.wear.compose.material.ChipDefaults
import coil.ImageLoader
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.request.SuccessResult
import com.example.gogoviet.data.DataProvider
import com.example.gogoviet.data.models.ContributedPlace
import com.example.gogoviet.data.models.PlacesCoreData
import com.example.gogoviet.data.models.PlacesRichData
import com.example.gogoviet.data.models.Review
import com.example.gogoviet.ui.theme.Teal1
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.Firebase
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.CompositeDecoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import java.lang.reflect.TypeVariable
import kotlin.collections.HashMap


var placesApiGlobal = emptyList<PlacesCoreData>()
var categoriesGlobal = emptySet<String>()

@Composable
fun ExploreScreen(context: Context, authViewModel: AuthViewModel) {
    MapScreen(context, authViewModel)
}

@Composable
fun FilterChips(categories: MutableSet<String>, selectedType: String, onTypeSelected: (String) -> Unit) {
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        items(categories.toTypedArray()) { type ->
            Chip(
                onClick = { onTypeSelected(type) },
                label = {
                    Text(text = type)
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

fun searchLocation(query: String, selectedType: String, placesApi: List<PlacesCoreData>): MutableList<PlacesCoreData> {
    val searchedPlaces = mutableListOf<PlacesCoreData>()

    placesApi.forEach {
        place -> run {
            if(place.name?.contains(query, ignoreCase = true) == true && (place.categories[0].name).contains(selectedType, ignoreCase = true)) {
                searchedPlaces.add(place)
            }
        }
    }

    return searchedPlaces
}

@SuppressLint("DiscouragedApi", "DefaultLocale")
@Composable
fun BottomSheetContent(place: PlacesCoreData, context: Context, authViewModel: AuthViewModel) {
    val db = FirebaseFirestore.getInstance()
    val placeCollection = db.collection("Places")

    val authState = authViewModel.authState.observeAsState()
    val userInfoState = authViewModel.userInfo.observeAsState(UserInfo())
    val userInfo = userInfoState.value

    var showDialog by remember { mutableStateOf(false) }
    var selectedStars by remember { mutableIntStateOf(0) }
    var comment by remember { mutableStateOf("") }
    var showShareDialog by remember { mutableStateOf(false) }

    var placesDetails: PlacesRichData? = PlacesRichData()
    var urls by remember { mutableStateOf(emptyList<String>()) }
    var images by remember { mutableStateOf(mapOf<String, ImageBitmap?>()) }

    var reviews by remember {
        mutableStateOf(emptyList<Review>())
    }

    LaunchedEffect(Unit) {
        placeCollection.document(place.fsqId!!).get().addOnSuccessListener {
            if(it.get("reviews") != null) {
                val reviewsArray = it.get("reviews") as? List<HashMap<String, Any>>
                if (reviewsArray != null) {
                    // Map HashMap to Review objects
                    reviews = reviewsArray.map { reviewMap ->
                        Review(
                            username = reviewMap["username"] as String,
                            comment = reviewMap["comment"] as String,
                            rating = (reviewMap["rating"] as Long).toInt()
                        )
                    }
                    println("Reviews: $reviews")
                } else {
                    println("No reviews found")
                }
            }
        }
    }

    LaunchedEffect(Unit) {
        place.fsqId.let {
            if (it != null) {
                DataProvider.getPlacesDetail(placeId = it) { it ->
                    placesDetails = it
                    urls = placesDetails?.photos?.map { "${it.prefix}800x600${it.suffix}" } ?: emptyList()
                }
            }
        }
    }

    images = rememberPlaceImages(urls, backgroundColor = Color.White.value.toInt())

    Column(modifier = Modifier
        .padding(16.dp)
        .verticalScroll(rememberScrollState())
    ) {
        place.name?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        place.location?.formattedAddress?.ifEmpty { "Chưa được cung cấp địa chỉ" }?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        placesDetails?.description?.let { Text(text = it, modifier = Modifier.padding(bottom = 10.dp)) }
        place.distance?.let { Text(text = "${String.format("%.2f", (it.toDouble() / 1000))}km") }

        Row(modifier = Modifier
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Chip(
                onClick = {
                    if(authState.value is AuthState.Unauthenticated) {
                        val intent = Intent(context, MainActivity::class.java)
                        intent.putExtra("screen", "login")
                        context.startActivity(intent)
                    }
                    else {
                        Toast.makeText(context, "Đã lưu địa điểm", Toast.LENGTH_LONG).show()
                    }
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
                    - ${place.location?.formattedAddress}
                    """.trimIndent(),
                onDismiss = { showShareDialog = false }
            )
        }

        LazyRow(
            modifier = Modifier
                .padding(vertical = 8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(images.keys.toTypedArray()) { key ->
                images[key]?.let {
                    println(" - Bottom Slide: $key")
                    Image(
                        painter = BitmapPainter(image = it),
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

        Text(text = "Đánh giá của khách thăm quan", style = MaterialTheme.typography.titleMedium)
        LazyRow(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(reviews) { review ->
                ReviewCard(review)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            Chip(
                onClick =
                {
                    if(authState.value is AuthState.Unauthenticated) {
                        val intent = Intent(context, MainActivity::class.java)
                        intent.putExtra("screen", "login")
                        context.startActivity(intent)
                    }
                    else {
                        showDialog = true
                    }
                },
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
                    val review: MutableMap<String, Any> = HashMap()

                    review["username"] = userInfo.name.ifEmpty { userInfo.email }
                    review["comment"] = commentText
                    review["rating"] = stars

                    placeCollection.document(place.fsqId!!).get()
                        .addOnSuccessListener {
                            if(it.exists()) {
                                placeCollection.document(place.fsqId!!)
                                    .update("reviews", FieldValue.arrayUnion(review))
                                    .addOnSuccessListener { println("add review success") }
                            }
                            else {
                                val addPlace: MutableMap<String, Any> = HashMap()
                                addPlace["reviews"] = listOf(review)
                                placeCollection.document(place.fsqId!!).set(addPlace)
                                    .addOnSuccessListener { println("add review success") }
                            }
                        }

                    selectedStars = stars
                    comment = commentText

                    println("Stars: $stars, Comment: $commentText")
                }
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            place.relatedPlaces?.let { it ->
                if(it.parent != null || it.children != null) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))
                    Text(text = "Gợi ý địa điểm", style = MaterialTheme.typography.titleMedium)
                }

                it.parent?.let {
                    SuggestionItem(place = it)
                }

                it.children?.forEach {
                    SuggestionItem(place = it)
                }
            }
        }
    }
}

@Composable
fun BottomSheetContentForContributedPlace(place: ContributedPlace, context: Context, authViewModel: AuthViewModel) {
    val db = FirebaseFirestore.getInstance()
    val contributedPlaceCollection = db.collection("Contributions")

    val authState = authViewModel.authState.observeAsState()
    val userInfoState = authViewModel.userInfo.observeAsState(UserInfo())
    val userInfo = userInfoState.value

    var showDialog by remember { mutableStateOf(false) }
    var selectedStars by remember { mutableIntStateOf(0) }
    var comment by remember { mutableStateOf("") }
    var showShareDialog by remember { mutableStateOf(false) }

    var imagesUrl by remember { mutableStateOf(emptyList<String>()) }
    var reviews by remember { mutableStateOf(emptyList<Review>()) }

    LaunchedEffect(Unit) {
        contributedPlaceCollection.document(place.id).get().addOnSuccessListener {
            CoroutineScope(Dispatchers.IO).launch {
                launch {
                    if(it.get("photos") != null) {
                        imagesUrl = it.get("photos") as List<String>
                    }
                }

                launch {
                    if(it.get("reviews") != null) {
                        val reviewsArray = it.get("reviews") as? List<HashMap<String, Any>>
                        if (reviewsArray != null) {
                            // Map HashMap to Review objects
                            reviews = reviewsArray.map { reviewMap ->
                                Review(
                                    username = reviewMap["username"] as String,
                                    comment = reviewMap["comment"] as String,
                                    rating = (reviewMap["rating"] as Long).toInt()
                                )
                            }
                            println("Reviews: $reviews")
                        } else {
                            println("No reviews found")
                        }
                    }
                }
            }
        }
    }

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
            text = "Đóng góp bởi: ${place.user}"
        )

        place.address.ifEmpty { "Chưa được cung cấp địa chỉ" }?.let {
            Text(
                text = it,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }

        Text(text = place.description, modifier = Modifier.padding(bottom = 10.dp))

        Row(modifier = Modifier
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Chip(
                onClick = {
                    if(authState.value is AuthState.Unauthenticated) {
                        val intent = Intent(context, MainActivity::class.java)
                        intent.putExtra("screen", "login")
                        context.startActivity(intent)
                    }
                    else {
                        Toast.makeText(context, "Đã lưu địa điểm", Toast.LENGTH_LONG).show()
                    }
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

        if(imagesUrl.isNotEmpty()) {
            LazyRow(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(imagesUrl) { url ->
                    println(" - Bottom Slide: $url")
                    Image(
                        painter = rememberAsyncImagePainter(url),
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

        Text(text = "Đánh giá của khách thăm quan", style = MaterialTheme.typography.titleMedium)
        LazyRow(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(reviews) { review ->
                ReviewCard(review)
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.fillMaxWidth()) {
            Chip(
                onClick =
                {
                    if(authState.value is AuthState.Unauthenticated) {
                        val intent = Intent(context, MainActivity::class.java)
                        intent.putExtra("screen", "login")
                        context.startActivity(intent)
                    }
                    else {
                        showDialog = true
                    }
                },
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
                    val review: MutableMap<String, Any> = HashMap()

                    review["username"] = userInfo.name.ifEmpty { userInfo.email }
                    review["comment"] = commentText
                    review["rating"] = stars

                    contributedPlaceCollection.document(place.id).get()
                        .addOnSuccessListener {
                            contributedPlaceCollection.document(place.id)
                                .update("reviews", FieldValue.arrayUnion(review))
                                .addOnSuccessListener {
                                    reviews = listOf(Review(review["username"].toString(), review["comment"].toString(), review["rating"].toString().toInt() )) + reviews
                                    println("add review success")
                                }
                        }

                    selectedStars = stars
                    comment = commentText

                    println("Stars: $stars, Comment: $commentText")
                }
            )
        }
    }
}

@SuppressLint("DiscouragedApi")
@Composable
fun SuggestionItem(place: PlacesCoreData) {
    Column {
        place.name?.let { Text(text = it, fontSize = 24.sp, fontWeight = FontWeight.Bold) }
        place.location?.formattedAddress.let {
            if (it != null) {
                Text(text = it)
            }
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
            Text(text = review.username, style = MaterialTheme.typography.titleMedium)
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

@SuppressLint("MutableCollectionMutableState", "PermissionLaunchedDuringComposition")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(context: Context, authViewModel: AuthViewModel) {
    var placesApi by remember {
        mutableStateOf(emptyList<PlacesCoreData>())
    }
    var categories by remember {
        mutableStateOf(mutableSetOf<String>())
    }
    var contributedPlaces by remember {
        mutableStateOf(emptyList<ContributedPlace>())
    }

    LaunchedEffect(Unit) {
        val db = FirebaseFirestore.getInstance()
        val contributionsCollection = db.collection("Contributions")

        contributionsCollection.get().addOnSuccessListener { it ->
            if (!it.isEmpty) {
                contributedPlaces = it.documents.mapNotNull { document ->
                    val contribution = document.toObject(ContributedPlace::class.java)
                    contribution?.let { it.id = document.id  }
                    contribution
                }
            }
        }
    }

    val authState = authViewModel.authState.observeAsState()
    val userInfoState = authViewModel.userInfo.observeAsState(UserInfo())
    val userInfo = userInfoState.value

    LaunchedEffect(Unit) {
        authViewModel.resetState() // Ensures state is reset only once when the screen loads
    }

    val fusedLocationProviderClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var clickedPlace by remember { mutableStateOf(PlacesCoreData(fsqId = "")) }
    var clickedContributedPlace by remember { mutableStateOf(ContributedPlace()) }
    val cameraPositionState = rememberCameraPositionState()
    val locationPermissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)

    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                location?.let { it ->
                    println("user location ${it.latitude} - ${it.longitude}")
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(LatLng(it.latitude, it.longitude), 15f)
                    DataProvider.getPlaces(LatLng(it.latitude, it.longitude), 100000) { it ->
                        placesApi = it
                        val categoriesTmp: MutableSet<String> = mutableSetOf()
                        it.forEach {
                            categoriesTmp.add(it.categories[0].name)
                        }
                        categories = categoriesTmp
                    }
                }
            }
        } else {
            locationPermissionState.launchPermissionRequest()
        }
    }

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }

    var contributedPlaceSheetState = rememberModalBottomSheetState()
    var showContributedPlaceBottomSheet by remember { mutableStateOf(false) }

    if(showBottomSheet) {
        ModalBottomSheet(
            sheetState = sheetState,
            onDismissRequest = {
                showBottomSheet = false
            },
        ) {
            BottomSheetContent(clickedPlace, context, authViewModel)
        }
    }

    if(showContributedPlaceBottomSheet) {
        ModalBottomSheet(
            sheetState = contributedPlaceSheetState,
            onDismissRequest = {
                showContributedPlaceBottomSheet = false
            },
        ) {
            BottomSheetContentForContributedPlace(clickedContributedPlace, context, authViewModel)
        }
    }

    var searchQuery by remember { mutableStateOf("") }
    var searchedPlaces by remember { mutableStateOf(emptyList<PlacesCoreData>()) }
    var isSearchBarActive by remember { mutableStateOf(false) }

    var selectedType by remember { mutableStateOf("") }

    LaunchedEffect(placesApi) {
        placesApiGlobal = placesApi
        searchedPlaces = placesApi
    }

    LaunchedEffect(categories) {
        categoriesGlobal = categories
    }


    Box(Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = MapProperties(isMyLocationEnabled = true)
        ) {
            val urls = placesApi.map { "${it.categories[0].icon.prefix}120${it.categories[0].icon.suffix}" }
            val markerIcons = rememberMarkerIcons(urls, backgroundColor = 0xFF2061C3.toInt())

            for(place in searchedPlaces) {
                val iconUrl = "${place.categories[0].icon.prefix}120${place.categories[0].icon.suffix}"
                val customIcon = markerIcons[iconUrl]

                println("Google map - ${place.geocodes!!.main.latitude}-${ place.geocodes.main.longitude} - ${iconUrl}")
                Marker(
                    state = MarkerState(position = LatLng(place.geocodes.main.latitude, place.geocodes.main.longitude)),
                    title = place.name,
                    icon = customIcon,
                    onClick = {
                        showBottomSheet = true
                        showContributedPlaceBottomSheet = false
                        clickedPlace = place
                        true
                    }
                )
            }

            for(place in contributedPlaces) {
                Marker(
                    state = MarkerState(position = LatLng(place.location.latitude, place.location.longitude)),
                    title = place.name,
                    icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker),
                    onClick = {
                        showBottomSheet = false
                        showContributedPlaceBottomSheet = true
                        clickedContributedPlace = place
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
                            searchedPlaces = searchLocation(searchQuery, searchQuery, placesApi)
                        } },
                        onSearch = {
                                str -> run {
                            isSearchBarActive = false
                            searchedPlaces = searchLocation(searchQuery, selectedType, placesApi)
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
                                                    LatLng(
                                                        clickedPlace.geocodes!!.main.latitude,
                                                        clickedPlace.geocodes!!.main.longitude
                                                    ),
                                                    15f
                                                )
                                            )
                                        )
                                        isSearchBarActive = false
                                    })
                                ) {
                                    place.name?.let { Text(text = it) }
                                }
                            }
                            }
                        }
                    }
                }
            }

            FilterChips(
                categories = categories,
                selectedType = selectedType,
                onTypeSelected = {
                    selectedType = if(selectedType == it) {
                        ""
                    } else {
                        it
                    }

                    searchedPlaces = searchLocation(searchQuery, selectedType, placesApi)
                }
            )

            Box(modifier = Modifier.fillMaxSize()) {
                FloatingActionButton(
                    onClick = {
                        val intent: Intent

                        if(authState.value is AuthState.Unauthenticated) {
                            intent = Intent(context, MainActivity::class.java)
                            intent.putExtra("screen", "login")
                        }
                        else {
                            intent = Intent(context, ContributeActivity::class.java)
                            intent.putExtra("user", userInfo.email )
                        }

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

@Composable
fun rememberMarkerIcons(urls: List<String>, backgroundColor: Int, padding: Int = 0): Map<String, BitmapDescriptor?> {
    val context = LocalContext.current
    var icons by remember { mutableStateOf<Map<String, BitmapDescriptor?>>(emptyMap()) }

    LaunchedEffect(urls) {
        icons = urls.associateWith { loadBitmapDescriptorFromUrlWithBackground(context, it, backgroundColor, padding) }
    }

    return icons
}

@Composable
fun rememberPlaceImages(urls: List<String>, backgroundColor: Int, padding: Int = 0): Map<String, ImageBitmap?> {
    val context = LocalContext.current
    var images by remember { mutableStateOf<Map<String, ImageBitmap?>>(emptyMap()) }

    LaunchedEffect(urls) {
        images = urls.associateWith { loadBitmapImageFromUrlWithBackground(context, it, backgroundColor, padding) }
    }

    return images
}
suspend fun loadBitmapImageFromUrlWithBackground(
    context: Context,
    url: String,
    backgroundColor: Int,
    padding: Int = 0
): ImageBitmap? {
    return withContext(Dispatchers.IO) {
        try {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(url)
                .allowHardware(false) // Disable hardware bitmaps.
                .build()

            val result = loader.execute(request)
            if (result is SuccessResult) {
                val originalBitmap = (result.drawable).toBitmap()
                // Thêm màu nền cho bitmap
                val bitmapWithBackground = fillBitmapBackgroundWithPadding(originalBitmap, backgroundColor, padding)
                bitmapWithBackground.asImageBitmap()
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

suspend fun loadBitmapDescriptorFromUrlWithBackground(
    context: Context,
    url: String,
    backgroundColor: Int,
    padding: Int = 0
): BitmapDescriptor? {
    return withContext(Dispatchers.IO) {
        try {
            val loader = ImageLoader(context)
            val request = ImageRequest.Builder(context)
                .data(url)
                .allowHardware(false) // Disable hardware bitmaps.
                .build()

            val result = loader.execute(request)
            if (result is SuccessResult) {
                val originalBitmap = (result.drawable).toBitmap()
                // Thêm màu nền cho bitmap
                val bitmapWithBackground = fillBitmapBackgroundWithPadding(originalBitmap, backgroundColor, padding)
                BitmapDescriptorFactory.fromBitmap(bitmapWithBackground)
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}

fun fillBitmapBackgroundWithPadding(bitmap: Bitmap, color: Int, padding: Int): Bitmap {
    val newWidth = bitmap.width + 2 * padding
    val newHeight = bitmap.height + 2 * padding

    val newBitmap = Bitmap.createBitmap(newWidth, newHeight, bitmap.config ?: Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(newBitmap)

    // Draw the background with rounded corners
    val rect = RectF(0f, 0f, newWidth.toFloat(), newHeight.toFloat())
    val path = Path().apply {
        addRoundRect(rect, 20F, 20F, Path.Direction.CW)
    }

    // Clip the canvas to rounded corners
    canvas.clipPath(path)

    // Vẽ màu nền
    canvas.drawColor(color)

    // Vẽ bitmap gốc lên canvas, với padding
    canvas.drawBitmap(bitmap, padding.toFloat(), padding.toFloat(), null)

    return newBitmap
}

