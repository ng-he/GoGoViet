    @file:OptIn(androidx.media3.common.util.UnstableApi::class)

    package com.example.gogoviet.ui.foryou

    import androidx.compose.animation.core.LinearEasing
    import androidx.compose.foundation.ExperimentalFoundationApi
    import androidx.compose.foundation.layout.fillMaxSize
    import androidx.compose.foundation.pager.VerticalPager
    import androidx.compose.foundation.pager.rememberPagerState
    import androidx.compose.material3.Text
    import androidx.compose.runtime.Composable
    import androidx.compose.runtime.LaunchedEffect
    import androidx.compose.runtime.key
    import androidx.compose.runtime.mutableStateOf
    import androidx.compose.runtime.remember
    import androidx.compose.ui.Modifier
    import androidx.hilt.navigation.compose.hiltViewModel
    import androidx.lifecycle.viewmodel.compose.viewModel

    import androidx.media3.common.util.UnstableApi
    import com.example.gogoviet.repository.VideoRepository
    import com.example.gogoviet.repository.VideoRepository_Factory
    import com.example.gogoviet.ui.video.VideoDetailScreen
    import com.example.gogoviet.ui.video.videoViewModel
    import dagger.hilt.android.lifecycle.HiltViewModel


    @OptIn(ExperimentalFoundationApi::class)
    @UnstableApi
    @Composable
    fun ListForYouVideoScreen(
        modifier: Modifier = Modifier,
        onShowComment: (Int) -> Unit
    ) {
        VerticalPager(state = rememberPagerState(initialPage = 0){10}, modifier = modifier) { videoId ->
            val viewModel: videoViewModel = hiltViewModel(key = videoId.toString())
            VideoDetailScreen(videoId = videoId, vieModel = viewModel, onShowComment = onShowComment)
        }
    }

//
//    @OptIn(ExperimentalFoundationApi::class, UnstableApi::class)
//    @Composable
//    @UnstableApi
//    fun ListForYouVideoScreen(
//        modifier: Modifier = Modifier,
//        onShowComment: (Int) -> Unit,
//        viewModel: videoViewModel = hiltViewModel()
//
//
//    ) {
//
//        val videoIds = viewModel.videos
//        val pagerState = rememberPagerState(initialPage = 0){videoIds.size}
//        val currentVideoIndex = remember { mutableStateOf(0) }
//        LaunchedEffect(pagerState.currentPage) {
//            // Khi trang hiện tại thay đổi, phát video tương ứng
//            viewModel.playVisibleVideo(videoIds[pagerState.currentPage])
//        }
//
//        VerticalPager(
//            state = pagerState,
//            modifier = modifier.fillMaxSize()
//        ) { videoId ->
//
//
//            val viewModel: videoViewModel = hiltViewModel(key = videoId.toString())
////            viewModel.playVisibleVideo(videoIds[pagerState.currentPage])
//
//            VideoDetailScreen(
//                videoId = videoId,
//                vieModel = viewModel,
//                onShowComment = onShowComment
//            )
//        }
//
//    }

