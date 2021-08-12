package com.project.findme.credentialactivity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.findme.mainactivity.MainActivity
import com.project.findme.utils.EventObserver
import com.ryan.findme.R
import com.ryan.findme.databinding.ActivityCredentialBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import android.widget.DatePicker

import android.app.DatePickerDialog.OnDateSetListener

import android.app.DatePickerDialog
import com.project.findme.utils.hideKeyboard
import java.text.SimpleDateFormat
import java.util.*


@AndroidEntryPoint
class CredentialActivity : AppCompatActivity() {

    private lateinit var viewModel: CredentialViewModel

    private lateinit var binding: ActivityCredentialBinding
    var interests = mutableSetOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(CredentialViewModel::class.java)
        subscribeToObserve()

        FirebaseAuth.getInstance().currentUser?.uid?.let {
            FirebaseFirestore.getInstance().collection("credentials").whereEqualTo("uid", it).get()
                .addOnSuccessListener { document ->
                    Log.d("QuerySnapShort", document.toString())
                    if (!document.isEmpty) {
                        Intent(this, MainActivity::class.java).also { intent ->
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        binding = ActivityCredentialBinding.inflate(layoutInflater)
                        setContentView(binding.root)

                        binding.apply {

                            credentialUsernameEt.setText(viewModel.name)
                            credentialProfessionEt.setText(viewModel.profession)

                            credentialUsernameEt.addTextChangedListener { name ->
                                viewModel.name = name.toString()
                            }

                            credentialDobEt.setOnClickListener {
                                hideKeyboard(this@CredentialActivity)
                                val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.US)
                                val newCalendar: Calendar = Calendar.getInstance()
                                DatePickerDialog(
                                    this@CredentialActivity,
                                    { _, year, monthOfYear, dayOfMonth ->
                                        val newDate: Calendar = Calendar.getInstance()
                                        newDate.set(year, monthOfYear, dayOfMonth)
                                        credentialDobEt.setText(dateFormatter.format(newDate.time))
                                    },
                                    newCalendar.get(Calendar.YEAR),
                                    newCalendar.get(Calendar.MONTH),
                                    newCalendar.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }

                            credentialProfessionEt.addTextChangedListener { profession ->
                                viewModel.profession = profession.toString()
                            }

                            credentialGenderRg.setOnCheckedChangeListener { _, checkedId ->
                                when (checkedId) {
                                    R.id.male_rb -> {
                                        viewModel.gender = maleRb.text.toString()
                                    }
                                    R.id.female_rb -> {
                                        viewModel.gender = femaleRb.text.toString()
                                    }
                                    R.id.other_rb -> {
                                        viewModel.gender = otherRb.text.toString()
                                    }
                                }
                            }

                            addBt.setOnClickListener {
                                val interest: String = credentialInterestEt.text.toString()
                                addChipToGroup(this@CredentialActivity, interest)
                            }

                            letsGoBt.setOnClickListener {
                                hideKeyboard(this@CredentialActivity)
                                viewModel.postCredential(
                                    uid = FirebaseAuth.getInstance().currentUser?.uid!!,
                                    dob = credentialDobEt.text.toString(),
                                    radioGroup = credentialGenderRg,
                                    interests = interests.toList()
                                )
                            }
                        }
                    }
                }
        }
    }

    private fun addChipToGroup(context: Context, interest: String) {
        val chip = Chip(context).apply {
            id = View.generateViewId()
            text = interest
            isClickable = true
            isCheckable = false
            isCloseIconVisible = true
        }

        interests.add(interest)

        binding.credentialHobbiesCg.addView(chip)

        chip.setOnCloseIconClickListener {
            interests.remove(chip.text.toString())
            binding.credentialHobbiesCg.removeView(chip)
        }
        binding.credentialInterestEt.text?.clear()
    }

    private fun subscribeToObserve() {
        viewModel.credentialPostStatus.observe(this, EventObserver(
            onError = {
                binding.apply {
                    credentialPb.isVisible = false
                    addBt.isEnabled = true
                    letsGoBt.isEnabled = true
                }
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            },
            onLoading = {
                binding.apply {
                    credentialPb.isVisible = true
                    addBt.isEnabled = false
                    letsGoBt.isEnabled = false
                }
            }
        ) {
            binding.credentialPb.isVisible = false
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
                finish()
            }
        })
    }
}