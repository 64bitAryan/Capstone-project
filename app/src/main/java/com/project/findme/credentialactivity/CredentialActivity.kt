package com.project.findme.credentialactivity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.findme.mainactivity.MainActivity
import com.project.findme.utils.EventObserver
import com.ryan.findme.databinding.ActivityCredentialBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*

@AndroidEntryPoint
class CredentialActivity: AppCompatActivity(){

    private lateinit var viewModel: CredentialViewModel

    private lateinit var bindin: ActivityCredentialBinding
    var interests = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CredentialViewModel::class.java)
        subscribeToObesrve()

        FirebaseAuth.getInstance().currentUser?.uid?.let {
            FirebaseFirestore.getInstance().collection("credentials").whereEqualTo("uid", it).get().addOnSuccessListener { document ->
                Log.d("QuerySnapShort", document.toString())
                if (!document.isEmpty) {
                    Intent(this, MainActivity::class.java).also { intent ->
                        startActivity(intent)
                        finish()
                    }
                } else {
                    bindin = ActivityCredentialBinding.inflate(layoutInflater)
                    setContentView(bindin.root)

                    bindin.apply {
                        addBt.setOnClickListener {
                            val interest: String = credentialInterestEt.text.toString()
                            addChipToGroup(this@CredentialActivity, interest)
                        }

                        letsGoBt.setOnClickListener {
                            val radio: RadioButton
                            val id: Int = credentialGenderRg.checkedRadioButtonId

                            radio = findViewById(id)

                            viewModel.postCredential(
                                uid = FirebaseAuth.getInstance().currentUser?.uid!!,
                                name = credentialUsernameEt.text.toString(),
                                profession =  credentialProfessionEt.text.toString(),
                                dob = credentialDobEt.text.toString(),
                                gender = radio.text.toString(),
                                interests = interests.toList()
                            )
                        }
                    }
                }
            }
        }
    }

    private fun addChipToGroup(context: Context, interest: String){
        val chip = Chip(context).apply {
            id = View.generateViewId()
            text = interest
            isClickable = true
            isCheckable = false
            isCloseIconVisible = true
        }

        interests.add(interest)

        bindin.credentialHobbiesCg.addView(chip)

        chip.setOnCloseIconClickListener {
            interests.remove(chip.text.toString())
            bindin.credentialHobbiesCg.removeView(chip)
        }
        bindin.credentialInterestEt.text?.clear()
    }
    private fun subscribeToObesrve(){
        viewModel.credentialPostStatus.observe(this, EventObserver(
            onError = {
                bindin.apply {
                    credentialPb.isVisible = false
                    addBt.isEnabled = true
                    letsGoBt.isEnabled = true
                }
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            },
            onLoading = {
                bindin.apply {
                    credentialPb.isVisible = true
                    addBt.isEnabled = false
                    letsGoBt.isEnabled = false
                }
            }
        ){
            bindin.credentialPb.isVisible = false
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
                finish()
            }
        })
    }
}