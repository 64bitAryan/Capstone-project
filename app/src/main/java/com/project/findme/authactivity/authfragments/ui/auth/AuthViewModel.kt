package com.project.findme.authactivity.authfragments.ui.auth

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val state: SavedStateHandle) :
    ViewModel() {

    private val authEventChannel = Channel<AuthViewModel.AuthEvent>()
    val authEvent = authEventChannel.receiveAsFlow()

    fun onSignInGoogleButtonClick(){

    }

    private fun showErrorMessage(text: String) = viewModelScope.launch {
        authEventChannel.send(AuthViewModel.AuthEvent.ShowErrorMessage(text))
    }

    private fun showPositiveMessage(text: String) = viewModelScope.launch {
        authEventChannel.send(AuthViewModel.AuthEvent.ShowPositiveMessage(text))
    }

    sealed class AuthEvent {
        data class ShowErrorMessage(val msg: String) : AuthEvent()
        data class ShowPositiveMessage(val msg: String) : AuthEvent()
    }
}