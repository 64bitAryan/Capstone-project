package com.project.findme.data.entity

import com.google.android.gms.common.util.CollectionUtils.listOf
import com.google.firebase.firestore.Exclude
import com.google.firebase.firestore.IgnoreExtraProperties
import com.project.findme.utils.Constants.DEFAULT_PROFILE_PICTURE_URL

@IgnoreExtraProperties
data class User (
    val uid: String = "",
    val userName: String = "",
    val profilePicture: String = DEFAULT_PROFILE_PICTURE_URL,
    val description: String = "",
    var follows: List<String> = listOf(),
    @get:Exclude
    var isFollowing: Boolean = false
)