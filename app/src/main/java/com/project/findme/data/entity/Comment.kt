package com.project.findme.data.entity

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties

@IgnoreExtraProperties
data class Comment(
    val commentId: String = "",
    val postId: String = "",
    val uid: String = "",
    @get:Exclude
    var username: String = "",
    @get:Exclude
    var profilePicture: String = "",
    val comment: String = "",
    val date: Long = System.currentTimeMillis(),
    val repliedComments: List<String> = listOf(),
    val parentId: String? = null
)