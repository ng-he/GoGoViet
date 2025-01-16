    @file:OptIn(androidx.media3.common.util.UnstableApi::class)

    package com.example.gogoviet.ui.foryou

    import androidx.compose.animation.core.LinearEasing
    import androidx.compose.foundation.ExperimentalFoundationApi
    import androidx.compose.foundation.background
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
    import androidx.compose.ui.graphics.Color
    import androidx.hilt.navigation.compose.hiltViewModel
    import androidx.lifecycle.viewmodel.compose.viewModel

    import androidx.media3.common.util.UnstableApi
    import com.example.gogoviet.repository.VideoRepository
    import com.example.gogoviet.repository.VideoRepository_Factory
    import com.example.gogoviet.ui.video.VideoDetailAction
    import com.example.gogoviet.ui.video.VideoDetailScreen
    import com.example.gogoviet.ui.video.videoViewModel
    import dagger.hilt.android.lifecycle.HiltViewModel


    @OptIn(ExperimentalFoundationApi::class, UnstableApi::class)
    @Composable
    fun ListForYouVideoScreen(
        modifier: Modifier = Modifier,
        onShowComment: (Int) -> Unit
    ) {
        val pagerState = rememberPagerState(initialPage = 0) { 10 } // 10 là số lượng video

        // Lưu ViewModel của từng video
        val viewModels = remember { mutableMapOf<Int, videoViewModel>() }

        VerticalPager(state = pagerState, modifier = modifier) { videoId ->
            // Lấy ViewModel cho từng video
            val viewModel: videoViewModel = viewModels.getOrPut(videoId) {
                hiltViewModel(key = videoId.toString())
            }

            // Màn hình chi tiết video
            VideoDetailScreen(videoId = videoId, vieModel = viewModel, onShowComment = onShowComment)
        }

        // Lắng nghe sự thay đổi của pagerState và xử lý việc dừng video khi chuyển trang
        LaunchedEffect(pagerState.currentPage) {
            // Gọi dispose để dừng video của trang trước đó
            viewModels.forEach { (id, viewModel) ->
                if (id != pagerState.currentPage) {
                    viewModel.processAction(VideoDetailAction.DisposeAction)
                }
            }

            // Phát video của trang hiện tại
            val currentVideoId = pagerState.currentPage
            viewModels[currentVideoId]?.processAction(VideoDetailAction.LoadData(currentVideoId))
        }
    }



//    @OptIn(ExperimentalFoundationApi::class)
//    @UnstableApi
//    @Composable
//    fun ListForYouVideoScreen(
//        modifier: Modifier = Modifier,
//        onShowComment: (Int) -> Unit
//    ) {
//        val pagerState = rememberPagerState(initialPage = 0 ){10}
//
//        VerticalPager(
//            state = pagerState,
//            modifier = modifier
//        ) { page ->
//            val viewModel: videoViewModel = hiltViewModel(key = page.toString())
//            VideoDetailScreen(
//                videoId = page,
//                vieModel = viewModel,
//                onShowComment = onShowComment
//            )
//        }
//    }


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

