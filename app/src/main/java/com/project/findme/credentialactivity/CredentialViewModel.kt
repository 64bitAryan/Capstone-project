package com.project.findme.credentialactivity

import android.content.Context
import android.widget.RadioGroup
import androidx.lifecycle.*
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
    val dispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val state: SavedStateHandle
) : ViewModel() {
    private val _credentialPostStatus = MutableLiveData<Events<Resource<Unit>>>()
    val credentialPostStatus: LiveData<Events<Resource<Unit>>> = _credentialPostStatus

    var name = state.get<String>("name") ?: ""
        set(value) {
            field = value
            state.set("name", value)
        }

    var profession = state.get<String>("profession") ?: ""
        set(value) {
            field = value
            state.set("profession", value)
        }

    var dob = state.get<String>("dob") ?: ""
        set(value) {
            field = value
            state.set("dob", value)
        }

    var gender = state.get<String>("gender") ?: ""
        set(value) {
            field = value
            state.set("gender", value)
        }


    fun postCredential(
        uid: String,
        interests: List<String>
    ) {
        val error =
            if (name.isBlank() || profession.isBlank() || interests.isEmpty()) {
                applicationContext.getString(R.string.error_input_empty)
            } else if (gender == "") {
                "Please select Gender"
            } else if (dob.isBlank()) {
                "Please Select D.O.B"
            } else null

        error?.let {
            _credentialPostStatus.postValue(Events(Resource.Error(it)))
            return
        }
        _credentialPostStatus.postValue(Events(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val credential = Credential(uid, name, profession, dob, gender, interests)
            val result = repository.postCredentials(credential)
            _credentialPostStatus.postValue(Events(Resource.Success(result)))
        }
    }
}