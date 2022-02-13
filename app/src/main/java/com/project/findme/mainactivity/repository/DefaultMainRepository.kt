package com.project.findme.mainactivity.repository

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.project.findme.data.entity.User
import com.project.findme.utils.Resource
import com.project.findme.utils.safeCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DefaultMainRepository : MainRepository {

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

    override suspend fun updatePassword(
        oldPassword: String,
        newPassword: String
    ): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            safeCall {
                val user = Firebase.auth.currentUser

                val credential = EmailAuthProvider
                    .getCredential(user?.email!!, oldPassword)

                val result = user.reauthenticate(credential).await()
                val result1 = user.updatePassword(newPassword).await()

                Resource.Success(result)
                Resource.Success(result1)
                Resource.Success(true)
            }
        }
    }
}