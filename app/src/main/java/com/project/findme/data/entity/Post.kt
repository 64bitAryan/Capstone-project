package com.project.findme.data.entity

import com.google.firebase.firestore.Exclude

data class Post(
    val id: String = "",
    val authorUid: String = "",
    @Exclude var authorUsername: String = "",
    @Exclude var authorProfilePictureUrl: String = "",
    val text: String = "",
    val imageUrl: String = "",
    val title: String = "",
    val date: Long = 0L,
    @Exclude var isLiked: Boolean = false,
    @Exclude var isLiking: Boolean = false,
    val likedBy: MutableList<String> = mutableListOf()
)