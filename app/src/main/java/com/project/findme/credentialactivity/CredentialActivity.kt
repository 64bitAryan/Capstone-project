package com.project.findme.credentialactivity

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.project.findme.mainactivity.MainActivity
import com.project.findme.utils.Constants.hobbies
import com.project.findme.utils.Constants.professions
import com.project.findme.utils.EventObserver
import com.project.findme.utils.hideKeyboard
import com.ryan.findme.R
import com.ryan.findme.databinding.ActivityCredentialBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class CredentialActivity : AppCompatActivity() {

    private lateinit var viewModel: CredentialViewModel

    private lateinit var binding: ActivityCredentialBinding
    private var interests = mutableSetOf<String>()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCredentialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel = ViewModelProvider(this).get(CredentialViewModel::class.java)
        subscribeToObserve()

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            professions
        )

        val adapterHobbies: ArrayAdapter<String> = ArrayAdapter<String>(
            this,
            android.R.layout.simple_dropdown_item_1line,
            hobbies
        )

        binding.apply {

            credentialProfessionEt.threshold = 1
            credentialProfessionEt.setAdapter(adapter)

            credentialInterestEt.threshold = 1
            credentialInterestEt.setAdapter(adapterHobbies)

            credentialUsernameEt.setText(viewModel.name)
            credentialProfessionEt.setText(viewModel.profession)

            credentialUsernameEt.addTextChangedListener { name ->
                viewModel.name = name.toString()
            }

            credentialDobEt.setOnClickListener {
                hideKeyboard(this@CredentialActivity)
                val dateFormatter = SimpleDateFormat("dd-MM-yyyy", Locale.US)
                val newCalendar: Calendar = Calendar.getInstance()
                val dialog = DatePickerDialog(
                    this@CredentialActivity,
                    { _, year, monthOfYear, dayOfMonth ->
                        val newDate: Calendar = Calendar.getInstance()
                        newDate.set(year, monthOfYear, dayOfMonth)
                        credentialDobEt.setText(dateFormatter.format(newDate.time))
                    },
                    newCalendar.get(Calendar.YEAR),
                    newCalendar.get(Calendar.MONTH),
                    newCalendar.get(Calendar.DAY_OF_MONTH)
                )

                dialog.datePicker.maxDate = newCalendar.timeInMillis
                dialog.show()

            }

            credentialDobEt.addTextChangedListener {
                viewModel.dob = it.toString()
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
                    interests = interests.toList()
                )
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
                    showProgress(false)
                }
                Toast.makeText(this, it, Toast.LENGTH_LONG).show()
            },
            onLoading = {
                binding.apply {
                    showProgress(true)
                }
            }
        ) {
            showProgress(false)
            Intent(this, MainActivity::class.java).also {
                startActivity(it)
                finish()
            }
        })
    }

    private fun showProgress(bool: Boolean) {
        binding.apply {
            cvProgressCredential.isVisible = bool
            if (bool) {
                parentLayoutCredential.alpha = 0.5f
                this@CredentialActivity.window!!.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            } else {
                parentLayoutCredential.alpha = 1f
                this@CredentialActivity.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = ActivityCredentialBinding.inflate(layoutInflater)
        binding.credentialUsernameEt.setText("")
        binding.credentialDobEt.setText("")
        binding.credentialProfessionEt.setText("")
        binding.credentialGenderRg.clearCheck()
        binding.credentialInterestEt.setText("")
        binding.credentialHobbiesCg.removeAllViews()
    }

}