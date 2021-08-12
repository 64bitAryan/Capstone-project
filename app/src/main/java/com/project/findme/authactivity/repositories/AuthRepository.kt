package com.project.findme.authactivity.repositories

import android.accounts.Account
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.firestore.auth.User
import com.project.findme.utils.Resource

interface AuthRepository {

    suspend fun register(email: String, username: String, password: String): Resource<AuthResult>

    suspend fun login(email: String, password: String): Resource<AuthResult>

    suspend fun forgotPassword(email: String): Resource<Boolean>

    suspend fun googleRegister(credentials: AuthCredential): Resource<AuthResult>

    suspend fun searchUser(query: String): Resource<List<com.project.findme.data.entity.User>>

}