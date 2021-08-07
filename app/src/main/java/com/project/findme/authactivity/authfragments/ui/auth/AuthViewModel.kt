package com.project.findme.authactivity.authfragments.ui.auth

import android.content.Context
import androidx.lifecycle.*
import com.google.firebase.auth.AuthResult
import com.project.findme.authactivity.repositories.AuthRepository
import com.project.findme.utils.Events
import com.project.findme.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val applicationContext: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) :
    ViewModel() {

    private val _googleRegisterStatus = MutableLiveData<Events<Resource<AuthResult>>>()
    val googleRegisterStatus: LiveData<Events<Resource<AuthResult>>> = _googleRegisterStatus

    fun onSignInGoogleButtonClick() {

        //No error Occurred
        //Giving Value to Loading Resource
        _googleRegisterStatus.postValue(Events(Resource.Loading()))

        //Already Given Resource Success in Repository
        viewModelScope.launch(dispatcher) {
            val result = repository.googleRegister()
            _googleRegisterStatus.postValue(Events(result))
        }
    }
}