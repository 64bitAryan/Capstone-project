package com.project.findme.mainactivity.mainfragments.ui.changePassword

import android.content.Context
import androidx.lifecycle.*
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
class ChangePasswordViewModel @Inject constructor(
    private val state: SavedStateHandle,
    private val repository: MainRepository,
    private val applicationContext: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val _changePasswordStatus = MutableLiveData<Events<Resource<Any>>>()
    val changePasswordStatus: LiveData<Events<Resource<Any>>> = _changePasswordStatus

    var oldPassword = state.get<String>("oldPassword") ?: ""
        set(value) {
            field = value
            state.set("oldPassword", value)
        }

    var newPassword = state.get<String>("newPassword") ?: ""
        set(value) {
            field = value
            state.set("newPassword", value)
        }

    fun changePassword() {

        val error = if (oldPassword.isEmpty() || newPassword.isEmpty()) {
            applicationContext.getString(R.string.error_input_empty)
        } else null

        error?.let {
            _changePasswordStatus.postValue(Events(Resource.Error(error)))
            return
        }

        _changePasswordStatus.postValue(Events(Resource.Loading()))

        viewModelScope.launch(dispatcher) {
            val result = repository.updatePassword(oldPassword, newPassword)
            _changePasswordStatus.postValue(Events(result))
        }
    }
}