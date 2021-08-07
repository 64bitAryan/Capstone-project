package com.project.findme.authactivity.authfragments.ui.register

import android.content.Context
import android.util.Patterns
import androidx.lifecycle.*
import com.google.firebase.auth.AuthResult
import com.project.findme.authactivity.repositories.AuthRepository
import com.project.findme.utils.Constants.MAX_USERNAME_LENGTH
import com.project.findme.utils.Constants.MIN_PASSWORD_LENGTH
import com.project.findme.utils.Constants.MIN_USERNAME_LENGTH
import com.project.findme.utils.Events
import com.project.findme.utils.Resource
import com.ryan.findme.R
import dagger.assisted.Assisted
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val repository: AuthRepository,
    private val applicationContext: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) :
    ViewModel() {

    private val _registerStatus = MutableLiveData<Events<Resource<AuthResult>>>()
    val registerStatus: LiveData<Events<Resource<AuthResult>>> = _registerStatus

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

    var username = state.get<String>("uname") ?: ""
        set(value) {
            field = value
            state.set("uname", value)
        }

    var repeatedPassword = state.get<String>("cpassword") ?: ""
        set(value) {
            field = value
            state.set("cpassword", value)
        }

    fun register(email: String, username: String, password: String, repeatedPassword: String){
        val error = if(email.isEmpty() || username.isEmpty() || password.isEmpty()){
            applicationContext.getString(R.string.error_input_empty)
        } else if (password != repeatedPassword) {
            applicationContext.getString(R.string.error_incorrect_repeated_password)
        } else if(username.length < MIN_USERNAME_LENGTH) {
            applicationContext.getString(R.string.error_username_too_short, MIN_USERNAME_LENGTH)
        } else if(username.length > MAX_USERNAME_LENGTH) {
            applicationContext.getString(R.string.error_username_too_long, MAX_USERNAME_LENGTH)
        } else if(password.length < MIN_PASSWORD_LENGTH) {
            applicationContext.getString(R.string.error_password_too_short, MIN_PASSWORD_LENGTH)
        } else if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            applicationContext.getString(R.string.error_not_a_valid_email)
        } else null

        //Error Occurred
        // If Error variable is Not empty
        error?.let {
            // Giving Value to Error Resource
            _registerStatus.postValue(Events(Resource.Error(error)))
            return
        }

        //No error Occurred
        //Giving Value to Loading Resource
        _registerStatus.postValue(Events(Resource.Loading()))

        //Already Given Resource Success in Repository
        viewModelScope.launch(dispatcher) {
            val result = repository.register(email, username, password)
            _registerStatus.postValue(Events(result))
        }
    }
}