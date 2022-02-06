package com.project.findme.authactivity.authfragments.ui.auth

import androidx.lifecycle.*
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.GoogleAuthProvider
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
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) :
    ViewModel() {

    private val _googleRegisterStatus = MutableLiveData<Events<Resource<AuthResult>>>()
    val googleRegisterStatus: LiveData<Events<Resource<AuthResult>>> = _googleRegisterStatus

    fun onSignInGoogleButtonClick(account: Task<GoogleSignInAccount>) {
        _googleRegisterStatus.postValue(Events(Resource.Loading()))
        account.let { googleAccount ->
            viewModelScope.launch(dispatcher){
                try {
                    val credentials = GoogleAuthProvider.getCredential(googleAccount.result.idToken, null)
                    val result = repository.googleRegister(credentials)
                    _googleRegisterStatus.postValue(Events(result))
                } catch (e: Exception){
                    _googleRegisterStatus.postValue(Events(Resource.Error("Please Select an Account")))
                }
            }
        }
    }
}