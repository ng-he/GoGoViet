package com.example.gogoviet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.MaterialTheme
//noinspection UsingMaterialAndMaterial3Libraries
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.media3.common.util.UnstableApi
import com.example.gogoviet.ui.foryou.ForYouVideoScreen
import com.example.gogoviet.ui.theme.GoGoVietTheme
import dagger.hilt.android.AndroidEntryPoint


//@UnstableApi
//@AndroidEntryPoint
//class VideoActivity: ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            GoGoVietTheme() {
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colors.background
//                ) {
//                    ForYouVideoScreen()
//                }
//            }
//        }
//    }
//}
@UnstableApi
@Composable

fun VideoScreen() {
    GoGoVietTheme {
        Surface (
            modifier = Modifier.fillMaxSize().background(Color.Black),
            color = Color.Black,
        ){
            ForYouVideoScreen()
        }
    }
}