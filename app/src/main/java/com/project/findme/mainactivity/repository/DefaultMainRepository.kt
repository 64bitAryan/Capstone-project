package com.project.findme.mainactivity.repository

import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.findme.data.entity.User
import com.project.findme.utils.Resource
import com.project.findme.utils.safeCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class DefaultMainRepository() : MainRepository {

    val auth = FirebaseAuth.getInstance()
    val users = FirebaseFirestore.getInstance().collection("users")
    val cred = FirebaseFirestore.getInstance().collection("credentials")

    override suspend fun searchUsers(query: String) = withContext(Dispatchers.IO) {
        safeCall {
            val userResult = users
                .orderBy("userName")
                .startAt(query)
                .endAt(query + "\uf8ff")
                .get().await().toObjects(User::class.java)
            Resource.Success(userResult)
        }
    }
}