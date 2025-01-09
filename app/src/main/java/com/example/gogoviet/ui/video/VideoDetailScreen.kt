package com.example.gogoviet.ui.video

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension

import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.example.gogoviet.designsystem.VideoPlayer
import com.example.gogoviet.ui.video.composables.VideoAttractiveInfo
import com.example.gogoviet.designsystem.VideoPlayer
import com.example.gogoviet.ui.video.composables.SideBarView
import com.example.gogoviet.ui.video.composables.VideoInfoArea
@UnstableApi
@Composable
fun VideoDetailScreen(
    videoId: Int,
    vieModel: videoViewModel,
    onShowComment: (Int) -> Unit
) {

    val uiState = vieModel.uiState.collectAsState()

    if (uiState.value == VideoDetailUiState.Default) {
        // loading
        vieModel.handleAction(VideoDetailAction.LoadData(videoId))
    }

    VideoDetailScreen(uiState = uiState.value, player = vieModel.player, onShowComment = {
        onShowComment(videoId)
    }) { aciton ->
        vieModel.handleAction(action = aciton)
    }
}

@UnstableApi
@Composable
fun VideoDetailScreen(
    uiState: VideoDetailUiState,
    player: ExoPlayer,
    onShowComment: () -> Unit,
    handleAction: (VideoDetailAction) -> Unit
) {
    when (uiState) {
        is VideoDetailUiState.Loading -> {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(text = "loading ...")
            }
        }

        is VideoDetailUiState.Success -> {
            VideoDetailScreen(
                player = player,
                handleAction = handleAction,
                onShowComment = onShowComment
            )
        }

        else -> {

        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@UnstableApi
@Composable
fun VideoDetailScreen(
    player: ExoPlayer,
    handleAction: (VideoDetailAction) -> Unit,
    onShowComment: () -> Unit
) {
    ConstraintLayout(modifier = Modifier
        .fillMaxSize()
        .clickable(
            onClick = {
                handleAction(VideoDetailAction.ToggleVideo)
            }
        )) {
        val (videoPlayerView, sideBar, videoInfo) = createRefs()
        VideoPlayer(player = player, modifier = Modifier.constrainAs(videoPlayerView) {
            top.linkTo(parent.top)
            bottom.linkTo(parent.bottom)
            start.linkTo(parent.start)
            end.linkTo(parent.end)
            width = Dimension.matchParent
            height = Dimension.matchParent
        })

        SideBarView(
            onAvatarClick = { /*TODO*/ },
            onLikeClick = { /*TODO*/ },
            onChatClick = onShowComment,
            onSaveClick = { /*TODO*/ },
            onShareClick = {},
            modifier = Modifier.constrainAs(sideBar) {
                end.linkTo(parent.end, margin = 16.dp)
                bottom.linkTo(parent.bottom, margin = 16.dp)
            }
        )
        VideoInfoArea(
            accountName = "account name",
            videoName = "videoname",
//            hashTags = listOf("jetpack compose", "android", "tiktok"),
//            songName = "Making my way",
            modifier = Modifier.constrainAs(videoInfo) {
                start.linkTo(parent.start, margin = 16.dp)
                bottom.linkTo(sideBar.bottom)
                end.linkTo(sideBar.start)
                width = Dimension.fillToConstraints
            }
        )
    }
}