package com.example.gogoviet.designsystem

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.ui.AspectRatioFrameLayout
import androidx.media3.ui.PlayerView
import com.example.gogoviet.ui.video.videoViewModel

@UnstableApi
@Composable
fun VideoPlayer(modifier: Modifier=Modifier,
                player: ExoPlayer
){
    val lifecycleOwner = LocalLifecycleOwner.current


    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_PAUSE -> {
                    // Tạm dừng video khi ứng dụng vào background
                    player.pause()
                }
                Lifecycle.Event.ON_STOP -> {
                    // Dừng hẳn video khi ứng dụng bị tắt
                    player.stop()
                }
                Lifecycle.Event.ON_RESUME -> {
                    // Tiếp tục phát video khi quay lại foreground
                    player.play()
                }
                else -> Unit
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }
    AndroidView(
        factory = { context ->
            PlayerView(context).also {
                it.player = player
                it.useController = false
//                it.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
    }
    }, modifier = modifier.background(Color.Black))

}