package com.project.findme.mainactivity.repository

import android.accounts.AuthenticatorDescription
import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.findme.data.entity.User
import com.project.findme.utils.Resource
import com.project.findme.utils.safeCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

interface MainRepository{

    suspend fun searchUsers(query: String): Resource<List<User>>

    suspend fun createPost(imageUri: Uri, title: String, description:String): Resource<Any>

}