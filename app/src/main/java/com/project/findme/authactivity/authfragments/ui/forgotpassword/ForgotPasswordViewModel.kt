package com.project.findme.authactivity.authfragments.ui.forgotpassword

import android.content.Context
import android.widget.Toast
import androidx.lifecycle.*
import com.google.firebase.auth.AuthResult
import com.project.findme.authactivity.repositories.AuthRepository
import com.project.findme.utils.Events
import com.project.findme.utils.Resource
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

    private val _forgotPasswordStatus = MutableLiveData<Events<Resource<Boolean>>>()
    val forgotPasswordStatus:LiveData<Events<Resource<Boolean>>> = _forgotPasswordStatus

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
            _forgotPasswordStatus.postValue(Events(Resource.Error(error)))
            return
        }

        _forgotPasswordStatus.postValue(Events(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.forgotPassword(email)
            _forgotPasswordStatus.postValue(Events(result))
        }
    }
}