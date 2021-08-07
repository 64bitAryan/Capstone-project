package com.project.findme.authactivity.repositories

import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.findme.data.entity.User
import com.project.findme.utils.Resource
import com.project.findme.utils.safeCall
import com.ryan.findme.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class DefaultAuthRepository: AuthRepository {

    private lateinit var googleSignInClient: GoogleSignInClient
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

    override suspend fun googleRegister(){
        return withContext(Dispatchers.IO){
            try{
                val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(R.string.default_web_client_id.toString())
                    .requestEmail()
                    .build()

                withContext(Dispatchers.Main){
                    googleSignInClient = GoogleSignIn.getClient(this, gso)
                }


            }catch (e : Exception){
                Log.d("Default Auth Activity: ", e.message.toString())
            }

        }
    }
}