package com.example.gogoviet.ui.video

//import androidx.lifecycle.ViewModel
//import androidx.media3.exoplayer.ExoPlayer
//import dagger.hilt.android.lifecycle.HiltViewModel
//import javax.inject.Inject

import android.content.Context
import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.media3.common.MediaItem
import androidx.media3.common.Player.REPEAT_MODE_ALL
import androidx.media3.common.util.UnstableApi
import androidx.media3.datasource.RawResourceDataSource
import androidx.media3.exoplayer.ExoPlayer
import com.example.gogoviet.repository.VideoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@UnstableApi
@HiltViewModel
class videoViewModel @Inject constructor(
    var player: ExoPlayer,
    private val videoRepository: VideoRepository,
//    savedStateHandle: SavedStateHandle
): ViewModel() {
//    val videoId: Int = savedStateHandle["videoId"] ?: 0
    private var _uiState = MutableStateFlow<VideoDetailUiState>(VideoDetailUiState.Default)
    val uiState: StateFlow<VideoDetailUiState>
        get() = _uiState


    init {
        Log.e("Frank", "VideoDetailViewModel init ")
        player.repeatMode = REPEAT_MODE_ALL
        player.playWhenReady = true
        player.prepare()
    }


    fun processAction(action: VideoDetailAction) {
        when (action) {
            is VideoDetailAction.LoadData -> {
                loadVideo(action.id)
            }
            is VideoDetailAction.ToggleVideo -> {
                toggleVideoPlayer()
            }
            is VideoDetailAction.DisposeAction -> {
                onDispose()
            }
            else -> {

            }
        }
    }
//    fun getListvideo()
//    {
//        return val video = videoRepository.getListvideo()
//    }
//    fun handleAction(action: VideoDetailAction) {
//        when (action) {
//            is VideoDetailAction.LoadData -> {
//                val videoId = action.id
//                loadVideo(videoId = videoId)
//            }
//
//            is VideoDetailAction.ToggleVideo -> {
//                toggleVideoPlayer()
//            }
//        }
//    }
    val videos = videoRepository.getListvideo()
    private fun loadVideo(videoId: Int) {
        _uiState.value = VideoDetailUiState.Loading
        viewModelScope.launch {
            delay(100L)
            val video = videoRepository.getVideo()
            playVideo(videoResourceId = video)
            _uiState.value = VideoDetailUiState.Success
        }
    }

    private fun playVideo(videoResourceId: Int) {
        val uri = RawResourceDataSource.buildRawResourceUri(videoResourceId)
        val mediaItem = MediaItem.fromUri(uri)
        player.setMediaItem(mediaItem)
        player.play()
    }

    private fun toggleVideoPlayer() {
        if (player.isLoading) {

        } else
            if (player.isPlaying) {
                player.pause()
            } else {
                player.play()
            }
    }
    private var currentPlayingVideoId: Int? = null

    fun playVisibleVideo(videoId: Int) {
        if (currentPlayingVideoId != videoId) {
            pauseVideo() // Dừng video cũ trước khi phát video mới
            currentPlayingVideoId = videoId
            loadVideo(videoId) // Tải và phát video mới
        }
    }


//    fun pauseVideo() {
//        currentPlayingVideoId?.let { videoId ->
//            // Giả sử bạn có một `Player` để phát video
//            player?.pause() // Dừng video đang phát
//        }
//        currentPlayingVideoId = null // Reset trạng thái
//    }



//    fun playVisibleVideo(videoId: Int) {
//        if (currentPlayingVideoId != videoId) {
//            currentPlayingVideoId = videoId
//            loadVideo(videoId)
//        }
//    }

//    private fun loadVideo(videoId: Int) {
//        viewModelScope.launch {
//            val videoResourceId = videoRepository.getVideo() // Lấy ID tài nguyên video
//            playVideo(videoResourceId)
//        }
//    }

//    private fun playVideo(videoResourceId: Int) {
//        val uri = RawResourceDataSource.buildRawResourceUri(videoResourceId)
//        val mediaItem = MediaItem.fromUri(uri)
//        player.setMediaItem(mediaItem)
//        player.prepare() // Đảm bảo ExoPlayer được chuẩn bị
//        player.play()    // Phát video
//    }


    private fun onDispose() {
        player.pause()
//        currentPlayingVideoId = null // Reset trạng thái video đang phát
    }


    override fun onCleared() {
//        Log.e("Frank", "VideoDetailViewModel onCleared")
        super.onCleared()
        player.release()
    }


fun pauseVideo() {
    if (player.isPlaying) {
        player.pause()
    }
}

}



sealed interface VideoDetailUiState {
    object Default: VideoDetailUiState
    object Loading: VideoDetailUiState
    object Success: VideoDetailUiState
    data class Error(val msg: String): VideoDetailUiState
}

sealed class VideoDetailAction {
    data class LoadData(val id: Int): VideoDetailAction()
    object ToggleVideo: VideoDetailAction()
    object DisposeAction: VideoDetailAction()
}