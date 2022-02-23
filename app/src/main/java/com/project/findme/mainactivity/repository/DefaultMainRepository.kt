package com.project.findme.mainactivity.repository

import android.net.Uri
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.project.findme.data.entity.Credential
import com.project.findme.data.entity.Post
import com.project.findme.data.entity.User
import com.project.findme.utils.Resource
import com.project.findme.utils.safeCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.*

class DefaultMainRepository() : MainRepository {

    val auth = FirebaseAuth.getInstance()
    val storage = Firebase.storage
    val users = FirebaseFirestore.getInstance().collection("users")
    val cred = FirebaseFirestore.getInstance().collection("credentials")
    val posts = FirebaseFirestore.getInstance().collection("posts")

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

    override suspend fun createPost(
        imageUri: Uri,
        title: String,
        description: String
    ): Resource<Any> = withContext(Dispatchers.IO) {
        safeCall {
            val uid = auth.uid!!
            val postId = UUID.randomUUID().toString()
            val imageUploadResult = storage.getReference(postId).putFile(imageUri).await()
            val imageUrl =
                imageUploadResult?.metadata?.reference?.downloadUrl?.await().toString()
            val post = Post(
                id = postId,
                authorUid = uid,
                imageUrl = imageUrl,
                title = title,
                text = description,
                date = System.currentTimeMillis()
            )
            posts.document(postId).set(post).await()
            Resource.Success(Any())
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

    override suspend fun updateProfile(
        username: String,
        description: String,
        profession: String,
        interests: List<String>
    ): Resource<Boolean> {
        return withContext(Dispatchers.IO) {
            safeCall {

                val user = Firebase.auth.currentUser
                val profileUpdate =
                    UserProfileChangeRequest.Builder().setDisplayName(username).build()

                val result = user!!.updateProfile(profileUpdate).await()
                val result1 = users.document(user.uid)
                    .update("userName", username, "description", description).await()
                val result2 = cred.document(user.uid)
                    .update("interest", interests, "profession", profession).await()

                val credential = cred.document(user.uid).get().await().toObject(Credential::class.java)
                val result3 = users.document(user.uid).update("credential", credential).await()

                Resource.Success(result)
                Resource.Success(result1)
                Resource.Success(result2)
                Resource.Success(result3)
                Resource.Success(true)
            }
        }
    }

    override suspend fun updateProfileUI(): Resource<User> = withContext(Dispatchers.IO) {
        safeCall {
            val uid = auth.currentUser!!.uid
            val user = users.document(uid).get().await().toObject(User::class.java)
            Resource.Success(user!!)
        }
    }

}