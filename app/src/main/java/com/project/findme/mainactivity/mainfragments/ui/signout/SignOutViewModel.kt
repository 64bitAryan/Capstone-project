package com.project.findme.mainactivity.mainfragments.ui.signout

import android.content.Context
import androidx.lifecycle.*
import com.project.findme.authactivity.repositories.AuthRepository
import com.project.findme.utils.Events
import com.project.findme.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignOutViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val applicationContext: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) :
    ViewModel() {

    private val _signOutStatus = MutableLiveData<Events<Resource<Boolean>>>()
    val signOutStatus: LiveData<Events<Resource<Boolean>>> = _signOutStatus

    fun onYesClick(){
        _signOutStatus.postValue(Events(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.signOut()
            _signOutStatus.postValue(Events(result))
        }
    }
}