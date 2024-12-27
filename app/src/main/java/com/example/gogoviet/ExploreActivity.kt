package com.example.gogoviet

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

@Composable
fun ExploreScreen() {
    val hanoi = LatLng(21.0278, 105.8342) // Example coordinates for Singapore
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(hanoi, 10f)
    }

    Column(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            /*            properties = MapProperties(mapType = MapType.HYBRID)*/
        ) {
            Marker(
                state = MarkerState(position = hanoi),
                title = "Hà Nội",
                snippet = "Thủ đô của Việt Nam"
            )
        }
    }
}