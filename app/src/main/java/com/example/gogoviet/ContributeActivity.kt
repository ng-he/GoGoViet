package com.example.gogoviet

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.wear.compose.material.Button
import androidx.wear.compose.material.OutlinedButton
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import com.example.gogoviet.data.models.PlacesCoreData
import com.example.gogoviet.ui.theme.GoGoVietTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberPermissionState
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ContributeActivity: ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val user = intent.getStringExtra("user")

        enableEdgeToEdge()
        setContent {
            GoGoVietTheme {
                ContributeScreen(this, user!!)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class, ExperimentalPermissionsApi::class)
@Composable
fun ContributeScreen(context: Context, user: String) {
    var placeName by remember { mutableStateOf("") }
    var placeDescription by remember { mutableStateOf("") }
    var placeCategory by remember { mutableStateOf("") }
    var placeAddress by remember { mutableStateOf("") }
    var placeLocation by remember { mutableStateOf(LatLng(0.0, 0.0)) }

    val fusedLocationProviderClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    val locationPermissionState = rememberPermissionState(permission = Manifest.permission.ACCESS_FINE_LOCATION)
    var selectedLocation by remember { mutableStateOf<LatLng?>(null) }
    val cameraPositionState = rememberCameraPositionState()

    var expanded by remember { mutableStateOf(false) }

    var selectedCategory by remember { mutableStateOf("") }

    var selectedImages by remember { mutableStateOf<List<Uri>>(emptyList()) }
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetMultipleContents()
    ) {
        uris -> selectedImages = uris
    }

    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        if (ActivityCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                location?.let {
                    println("user location ${it.latitude} - ${it.longitude}")
                    selectedLocation = LatLng(it.latitude, it.longitude)
                    placeLocation = LatLng(it.latitude, it.longitude)
                    cameraPositionState.position = CameraPosition.fromLatLngZoom(selectedLocation!!, 15f)
                }
            }
        } else {
            locationPermissionState.launchPermissionRequest()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Đóng góp") },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            goBack(context)
                        }
                    ) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Text(
                text = "Thông tin chi tiết về địa điểm",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Cung cấp 1 số thông tin về địa điểm này. Địa điểm này sẽ xuất hiện công khai nếu được thêm vào Maps",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Input Fields
            InputField(label = "Tên địa điểm (bắt buộc)*", onValueChange = {str -> run { placeName = str }})
            Spacer(modifier = Modifier.height(8.dp))
            InputField(label = "Mô tả", onValueChange = { str -> run { placeDescription = str }})
            Spacer(modifier = Modifier.height(8.dp))

            // drop down list
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = { },
                    label = { Text("Danh mục (bắt buộc)*") },
                    trailingIcon = {
                        Icon(
                            imageVector = if (expanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                            contentDescription = null
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categoriesGlobal.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(text = category) },
                            onClick = {
                                selectedCategory = category
                                placeCategory = category
                                expanded = false
                            }
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(8.dp))

            InputFieldWithIcon(
                label = "Địa chỉ (bắt buộc)*",
                icon = Icons.Default.LocationOn,
                onValueChange = {str -> run { placeAddress = str }}
            )
            Spacer(modifier = Modifier.height(16.dp))

            // Map View Placeholder
            Text(
                text = "Vị trí của địa điểm",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
                    .border(1.dp, Color.Gray)
            ) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = true),
                    onMapClick = { latLng ->
                        selectedLocation = latLng
                        placeLocation = latLng
                    }
                ) {
                    selectedLocation?.let {
                        Marker(
                            state = MarkerState(position = it),
                            title = "Selected Location",
                            snippet = "${it.latitude}, ${it.longitude}"
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            // Image Picker
            Button(
                onClick = {
                    imagePickerLauncher.launch("image/*")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Chọn hình ảnh")
            }

            // Display selected images
            LazyRow(modifier = Modifier.padding(top = 8.dp)) {
                items(selectedImages) {
                    uri -> Image(
                        painter = rememberAsyncImagePainter(uri),
                        contentDescription = null,
                        modifier = Modifier
                            .size(80.dp)
                            .padding(end = 8.dp)
                            .clip(MaterialTheme.shapes.small)
                            .border(1.dp, Color.Gray)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedButton(
                    onClick = {
                        goBack(context)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Hủy")
                }
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = {
                        val db = FirebaseFirestore.getInstance()
                        val contributionsCollection = db.collection("Contributions")
                        val contributionPlace: MutableMap<String, Any> = HashMap()

                        contributionPlace["name"] = placeName
                        contributionPlace["description"] = placeDescription
                        contributionPlace["category"] = placeCategory
                        contributionPlace["address"] = placeAddress
                        contributionPlace["location"] = hashMapOf(
                            "latitude" to placeLocation.latitude,
                            "longitude" to placeLocation.longitude
                        )

                        contributionPlace["user"] = user

                        if(selectedImages.isNotEmpty()) {
                            coroutineScope.launch {
                                val photoUrls = selectedImages.map { uri ->
                                    async {
                                        uploadImageToFirebase(uri)
                                    }
                                }.awaitAll().filterNotNull() // Collect all successful URLs

                                contributionPlace["photos"] = photoUrls
                                addPlace(context, contributionsCollection, contributionPlace)
                            }
                        }
                        else {
                            addPlace(context, contributionsCollection, contributionPlace)
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(text = "Gửi")
                }
            }
        }
    }
}

fun addPlace(context: Context, collection: CollectionReference, place: MutableMap<String, Any>) {
    collection.document().set(place)
        .addOnSuccessListener {
        println("Contribution added successfully!")
        goBack(context) // Navigate back after saving
        }
        .addOnFailureListener { e ->
            println("Failed to add contribution: ${e.message}")
        }
}

fun goBack(context: Context) {
    val intent = Intent(context, MainActivity::class.java)
    intent.putExtra("screen", "explore")
    context.startActivity(intent)
}

suspend fun uploadImageToFirebase(uri: Uri): String? {
    return try {
        // Get Firebase Storage instance
        val storageReference = FirebaseStorage.getInstance().reference

        // Create a unique name for the file
        val fileName = "images/${UUID.randomUUID()}.jpg"
        val fileReference = storageReference.child(fileName)

        // Upload the file
        val uploadTask = fileReference.putFile(uri).await()

        // Get the download URL
        fileReference.downloadUrl.await().toString()
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}

fun saveImageUrlToFirestore(url: String) {
    val db = FirebaseFirestore.getInstance()
    val imageDoc = hashMapOf("url" to url)

    db.collection("images")
        .add(imageDoc)
        .addOnSuccessListener { println("Image URL saved successfully!") }
        .addOnFailureListener { e -> println("Error saving URL: ${e.message}") }
}

@Composable
fun InputField(label: String, onValueChange: (String) -> Unit) {
    var input by remember { mutableStateOf("") }

    OutlinedTextField(
        value = input,
        onValueChange = {
            input = it
            onValueChange(input)
        },
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small
    )
}

@Composable
fun InputFieldWithIcon(label: String, icon: ImageVector, onValueChange: (String) -> Unit) {
    var input by remember { mutableStateOf("") }

    OutlinedTextField(
        value = input,
        onValueChange = {
            input = it
            onValueChange(input)
        },
        label = { Text(label) },
        trailingIcon = {
            Icon(imageVector = icon, contentDescription = null)
        },
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.small
    )
}