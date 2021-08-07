package com.project.findme.authactivity.repositories

import com.google.firebase.auth.AuthResult
import com.project.findme.utils.Resource

interface AuthRepository {

    suspend fun register(email: String, username: String, password: String): Resource<AuthResult>

    suspend fun login(email: String, password: String): Resource<AuthResult>

    suspend fun forgotPassword(email: String): Resource<Boolean>

}