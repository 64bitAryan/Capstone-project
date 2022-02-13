package com.project.findme.mainactivity.repository

import com.project.findme.data.entity.User
import com.project.findme.utils.Resource

interface MainRepository {

    suspend fun searchUsers(query: String): Resource<List<User>>

    suspend fun updatePassword(oldPassword: String, newPassword: String): Resource<Boolean>

    suspend fun updateProfile(
        username: String,
        description: String,
        profession: String,
        interests: List<String>
    ): Resource<Boolean>

}