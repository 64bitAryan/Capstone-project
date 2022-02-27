package com.project.findme.credentialactivity.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.findme.data.entity.Credential
import com.project.findme.utils.Resource
import com.project.findme.utils.safeCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DefaultCredentialRepository: CredentialRepository {

    val auth = FirebaseAuth.getInstance()
    val cred = FirebaseFirestore.getInstance().collection("credentials")
    val users = FirebaseFirestore.getInstance().collection("users")

    override suspend fun postCredentials(
        credential: Credential
    ) {
        return withContext(Dispatchers.IO){
            safeCall {
                val uid = auth.currentUser?.uid!!
                val result = cred.document(uid).set(credential).await()
                users.document(uid).update("credential", credential).await()
                Resource.Success(result)
            }
        }
    }
}