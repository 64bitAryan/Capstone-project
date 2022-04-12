package com.project.findme.data.entity

import android.net.Uri
import androidx.core.net.toUri

data class UpdateUser(
    val uidToUpdate: String = "",
    val userName: String = "",
    val description: String = "",
    var updateCredential: UpdateCredentials = UpdateCredentials(
        profession = "",
        interest = listOf()
    ),
    val profilePicture: Uri? = "https://firebasestorage.googleapis.com/v0/b/social-network-662a2.appspot.com/o/avatar.png?alt=media&token=69f56ce2-8fe2-4051-9e61-9e52182384c9".toUri()
)