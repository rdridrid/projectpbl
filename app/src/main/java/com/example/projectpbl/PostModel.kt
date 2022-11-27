package com.example.projectpbl

import java.time.LocalDateTime

data class PostModel(
    val title : String,
    val content : String,
    val uid : String,
    val time : LocalDateTime,
    var imgUri: Int,
)
