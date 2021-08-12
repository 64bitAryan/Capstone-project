package com.project.findme.credentialactivity

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.findme.credentialactivity.repository.CredentialRepository
import com.project.findme.data.entity.Credential
import com.project.findme.utils.Events
import com.project.findme.utils.Resource
import com.ryan.findme.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CredentialViewModel @Inject constructor(
    val repository: CredentialRepository,
    val applicationContext: Context,
    val dispatcher: CoroutineDispatcher = Dispatchers.Main
): ViewModel() {
    private val _credentialPostStatus = MutableLiveData<Events<Resource<Unit>>>()
    val credentialPostStatus:LiveData<Events<Resource<Unit>>> = _credentialPostStatus

    fun postCredential(
        uid: String,
        name: String,
        profession: String,
        dob: String,
        gender: String,
        interests:List<String>
    ){
        val error = if(name.isEmpty() || profession.isEmpty() || dob.isEmpty() || gender.isEmpty() || interests.isEmpty()){
            applicationContext.getString(R.string.error_input_empty)
        } else null

        error?.let {
            _credentialPostStatus.postValue(Events(Resource.Error(it)))
        }
        _credentialPostStatus.postValue(Events(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val credential = Credential(uid, name, profession, dob, gender, interests)
            val result = repository.postCredentials(credential)
            _credentialPostStatus.postValue(Events(Resource.Success(result)))
        }
    }
}