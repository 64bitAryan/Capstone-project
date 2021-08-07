package com.project.findme.authactivity.authfragments.ui.forgotpassword

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.findme.authactivity.repositories.AuthRepository
import com.ryan.findme.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val repository: AuthRepository,
    private val applicationContext: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) :
    ViewModel() {

    var email = state.get<String>("email") ?: ""
        set(value) {
            field = value
            state.set("email", value)
        }

    fun onForgotPasswordConfirmClick() {
        val error = if (email.isBlank()) {
            applicationContext.getString(R.string.error_input_empty)
        } else null

        //Error Occurred
        // If Error variable is Not empty
        error?.let {
            // Giving Value to Error Resource
            Toast.makeText(applicationContext, it, Toast.LENGTH_LONG).show()
            return
        }
        viewModelScope.launch(dispatcher) {
            repository.forgotPassword(email)
        }
    }
}