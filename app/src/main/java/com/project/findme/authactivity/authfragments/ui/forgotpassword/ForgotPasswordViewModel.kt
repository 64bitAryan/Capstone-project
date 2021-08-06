package com.project.findme.authactivity.authfragments.ui.forgotpassword

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(private val state: SavedStateHandle) :
    ViewModel() {

    private val auth = Firebase.auth

    var email = state.get<String>("email") ?: ""
        set(value) {
            field = value
            state.set("email", value)
        }

    private val forgotPasswordEventChannel = Channel<ForgotPasswordEvent>()
    val forgotPasswordEvent = forgotPasswordEventChannel.receiveAsFlow()

    fun onForgotPasswordConfirmClick(mail : String){
        auth.sendPasswordResetEmail(mail)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    showPositiveMessage("Password reset link sent to your email.")
                }
                else{
                    showErrorMessage(it.exception?.message.toString())
                }
            }
    }

    private fun showErrorMessage(text: String) = viewModelScope.launch {
        forgotPasswordEventChannel.send(ForgotPasswordEvent.ShowErrorMessage(text))
    }

    private fun showPositiveMessage(text: String) = viewModelScope.launch {
        forgotPasswordEventChannel.send(ForgotPasswordEvent.ShowPositiveMessage(text))
    }

    sealed class ForgotPasswordEvent {
        data class ShowErrorMessage(val msg: String) : ForgotPasswordEvent()
        data class ShowPositiveMessage(val msg: String) : ForgotPasswordEvent()
    }

}