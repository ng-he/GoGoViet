package com.example.gogoviet.repository

import com.example.gogoviet.R
import javax.inject.Inject

class VideoRepository @Inject constructor() {

    public val videos = listOf(
        R.raw.video1,
        R.raw.video2,
        R.raw.video3,
        R.raw.video4

    )
    fun getVideo() = videos.random()

    fun getListvideo() = videos
}