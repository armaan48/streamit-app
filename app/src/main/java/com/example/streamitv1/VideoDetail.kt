package com.example.streamitv1


data class VideoDetail(
    val str: String,
    val id: String,
    val author: UserDetail,
    val title: String,
    val tags: String,
    val description: String,
    val videoURL1: String,
    val videoURL2: String,
    val videoThumbnail: String
)
