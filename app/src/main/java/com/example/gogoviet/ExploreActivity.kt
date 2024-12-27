package com.example.gogoviet

import android.annotation.SuppressLint
import android.content.res.Resources
import android.graphics.BitmapFactory
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role.Companion.Button
import androidx.compose.ui.unit.dp
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExploreScreen() {
    val vanMieu = LatLng(21.028256689682053, 105.83567826735033) // Example coordinates for Singapore
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(vanMieu, 15f)
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
            BottomSheetContent(
                title = "Văn Miếu Quốc Tử Giám",
                reviews = listOf(
                    Review("Hùng Nguyễn", "Nơi tuyệt vời với các tiện nghi tuyệt vời", 5),
                    Review("Tuấn Anh", "Điểm hoàn hảo cho chuyến đi chơi gia đình", 4)
                ),
                suggestions = listOf(
                    Suggestion("Địa danh lịch sử", "Các tour du lịch có hướng dẫn có sẵn", R.drawable.ic_castle),
                    Suggestion("Vườn quốc gia Ba Vì", "Có rất nhiều loại thực vật", R.drawable.ic_tree)
                )
            )
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState
        ) {
            Marker(
                state = MarkerState(position = vanMieu),
                title = "Văn Miếu Quốc Tử Giám",
                snippet = "Trường Đại Học đầu tiên",
                icon = BitmapDescriptorFactory.fromResource(R.drawable.ic_map_marker),
                onClick = {
                    showBottomSheet = true
                    true
                }
            )
        }
    }
}

@Composable
fun BottomSheetContent(
    title: String,
    reviews: List<Review>,
    suggestions: List<Suggestion>
) {
    Column(modifier = Modifier
        .padding(16.dp)
        .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        Image(painter = painterResource(id = R.drawable.vanmieu), contentDescription = "Văn Miếu",
            modifier = Modifier.clip(shape = RoundedCornerShape(
                topStart = 16.dp,
                topEnd = 16.dp,
                bottomStart = 16.dp,
                bottomEnd = 16.dp)
            )
        )

        Text(text = "Đánh giá của khách thăm quan", style = MaterialTheme.typography.titleMedium)

        LazyRow(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(reviews) { review ->
                ReviewCard(review)
            }
        }

        Button(
            onClick = {},
            colors = ButtonDefaults.run { buttonColors(containerColor = Color.Blue) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
        ) {
            Text(text = "Lưu địa điểm", color = Color.White)
        }

        OutlinedButton(
            onClick = {},
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Chia sẻ")
        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 16.dp))

        Text(text = "Gợi ý địa điểm", style = MaterialTheme.typography.titleMedium)

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            suggestions.forEach { suggestion ->
                SuggestionItem(suggestion)
            }
        }
    }
}

@Composable
fun SuggestionItem(suggestion: Suggestion) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
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
