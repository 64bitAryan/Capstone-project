package com.project.findme

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.findme.authactivity.AuthActivity
import com.project.findme.credentialactivity.CredentialActivity
import com.project.findme.mainactivity.MainActivity
import com.ryan.findme.R

class SplashScreen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        if (FirebaseAuth.getInstance().currentUser != null) {
            FirebaseAuth.getInstance().currentUser?.uid?.let {
                FirebaseFirestore.getInstance().collection("credentials").whereEqualTo("uid", it)
                    .get()
                    .addOnSuccessListener { document ->
                        if (!document.isEmpty) {
                            Intent(this, MainActivity::class.java).also { intent ->
                                startActivity(intent)
                                finish()
                            }
                        } else {
                            Intent(this, CredentialActivity::class.java).also { intent ->
                                startActivity(intent)
                                finish()
                            }
                        }
                    }
            }
        } else {
            Intent(this, AuthActivity::class.java).also { intent ->
                startActivity(intent)
                finish()
            }
        }

    }
}