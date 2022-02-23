package com.project.findme.mainactivity.mainfragments.ui.editProfile

import android.content.Context
import androidx.lifecycle.*
import com.project.findme.data.entity.User
import com.project.findme.mainactivity.repository.MainRepository
import com.project.findme.utils.Events
import com.project.findme.utils.Resource
import com.ryan.findme.R
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val repository: MainRepository,
    private val applicationContext: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val _updateProfileStatus = MutableLiveData<Events<Resource<Boolean>>>()
    val updateProfileStatus: LiveData<Events<Resource<Boolean>>> = _updateProfileStatus

    private val _updateUIStatus = MutableLiveData<Events<Resource<User>>>()
    val updateUIStatus: LiveData<Events<Resource<User>>> = _updateUIStatus

    var username = state.get<String>("username") ?: ""
        set(value) {
            field = value
            state.set("username", value)
        }

    var description = state.get<String>("description") ?: ""
        set(value) {
            field = value
            state.set("description", value)
        }

    var profession = state.get<String>("profession") ?: ""
        set(value) {
            field = value
            state.set("profession", value)
        }

    fun updateProfile(interests: List<String>) {

        val error = if (username.isEmpty() || description.isEmpty() || profession.isEmpty()) {
            applicationContext.getString(R.string.error_input_empty)
        } else null

        error?.let {
            _updateProfileStatus.postValue(Events(Resource.Error(error)))
            return
        }

        _updateProfileStatus.postValue(Events(Resource.Loading()))

        viewModelScope.launch(dispatcher) {
            val result = repository.updateProfile(username, description, profession, interests)
            _updateProfileStatus.postValue(Events(result))
        }
    }

    fun updateUI(){
        _updateUIStatus.postValue(Events(Resource.Loading()))

        viewModelScope.launch(dispatcher) {
            val user = repository.updateProfileUI()
            _updateUIStatus.postValue(Events(user))
        }
    }

}