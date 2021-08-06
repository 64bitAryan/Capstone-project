package com.project.findme.authactivity.authfragments.ui.login

import android.content.Context
import androidx.lifecycle.*
import com.google.firebase.auth.AuthResult
import com.project.findme.authactivity.repositories.AuthRepository
import com.project.findme.utils.Events
import com.project.findme.utils.Resource
import com.ryan.findme.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val applicationContext: Context,
    private val repository: AuthRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val state: SavedStateHandle
    ) :
    ViewModel() {

    private val _loginStatus = MutableLiveData<Events<Resource<AuthResult>>>()
    val loginStatus: LiveData<Events<Resource<AuthResult>>> = _loginStatus

    var email = state.get<String>("email") ?: ""
        set(value) {
            field = value
            state.set("email", value)
        }

    var password = state.get<String>("password") ?: ""
        set(value) {
            field = value
            state.set("password", value)
        }

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