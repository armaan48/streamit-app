package com.example.streamitv1

data class VideoDetail(
    val id: String,
    val author: UserDetail,
    val title: String,
    val tags: String,
    val description: String,
    val videoURL: String,
    val videoThumbnail: String
)
