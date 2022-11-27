package com.example.projectpbl

import android.graphics.Bitmap
import java.time.LocalDateTime

data class PostModel(
    val title : String,
    val content : String,
    val name : String,
    val time : String,
    val email : String,
    val imageUri : String,
    val useruid : String,
    val profileimageUri : String
)
