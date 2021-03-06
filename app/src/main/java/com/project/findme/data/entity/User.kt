package com.project.findme.data.entity

import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.project.findme.utils.Constants.DEFAULT_PROFILE_PICTURE_URL

@IgnoreExtraProperties
data class User(
    val uid: String = "",
    val userName: String = "",
    val profilePicture: String = DEFAULT_PROFILE_PICTURE_URL,
    val description: String = "",
    var follows: List<String> = listOf(),
    var followings: List<String> = listOf(),
    var credential: Credential = Credential(
        uid = "",
        name = "",
        profession = "",
        dob = "",
        gender = "",
        interest = listOf()
    ),
    @get:Exclude
    var isFollowing: Boolean = false
)