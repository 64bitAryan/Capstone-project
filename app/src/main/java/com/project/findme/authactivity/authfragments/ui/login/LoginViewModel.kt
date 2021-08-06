package com.project.findme.authactivity.authfragments.ui.login

import android.content.Context
import android.os.Bundle
import androidx.lifecycle.*
import androidx.savedstate.SavedStateRegistryOwner
import com.google.firebase.auth.AuthResult
import com.project.findme.authactivity.repositories.AuthRepository
import com.project.findme.utils.Events
import com.project.findme.utils.Resource
import com.ryan.findme.R
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val applicationContext: Context,
    private val repository: AuthRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
    ) :
    ViewModel() {


    private val _loginStatus = MutableLiveData<Events<Resource<AuthResult>>>()
    val loginStatus: LiveData<Events<Resource<AuthResult>>> = _loginStatus

    fun login(email: String, password: String){
        if(password.isEmpty() || email.isEmpty()){
            val error = applicationContext.getString(R.string.error_input_empty)
            _loginStatus.postValue(Events(Resource.Error(error)))
        } else {
            _loginStatus.postValue(Events(Resource.Loading()))
            viewModelScope.launch(dispatcher) {
                val result = repository.login(email, password)
                _loginStatus.postValue(Events(result))
            }
        }
    }
}