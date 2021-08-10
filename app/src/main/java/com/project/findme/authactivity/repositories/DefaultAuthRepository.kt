package com.project.findme.authactivity.repositories

import android.accounts.Account
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.project.findme.data.entity.User
import com.project.findme.utils.Resource
import com.project.findme.utils.safeCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DefaultAuthRepository: AuthRepository {

    val auth = FirebaseAuth.getInstance()
    val users = FirebaseFirestore.getInstance().collection("users")

    override suspend fun register(
        email: String,
        username: String,
        password: String
    ): Resource<AuthResult> {
        return withContext(Dispatchers.IO){
            safeCall {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val uid = result.user?.uid!!
                val user = User(uid, username)
                users.document(uid).set(user).await()
                Resource.Success(result)
            }
        }
    }

    override suspend fun login(email: String, password: String): Resource<AuthResult> {
        return  withContext(Dispatchers.IO) {
            safeCall {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                Resource.Success(result)
            }
        }
    }

    override suspend fun forgotPassword(email: String):Resource<Boolean> {
         return withContext(Dispatchers.IO) {
             safeCall {
                 val result = auth.sendPasswordResetEmail(email).await()
                 Resource.Success(result)
                 Resource.Success(true)
             }
        }
    }

    override suspend fun googleRegister(credentials: AuthCredential): Resource<AuthResult>{
        return withContext(Dispatchers.IO) {
            safeCall {
                val result = auth.signInWithCredential(credentials).await()
                Resource.Success(result)
            }
        }
    }
}