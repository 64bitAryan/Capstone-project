package com.project.findme.mainactivity.mainfragments.ui.editProfile

import android.content.Context
import android.media.metrics.Event
import android.net.Uri
import androidx.lifecycle.*
import com.project.findme.data.entity.UpdateUser
import com.project.findme.data.entity.User
import com.project.findme.mainactivity.repository.MainRepository
import com.project.findme.utils.Events
import com.project.findme.utils.Resource
import com.ryan.findme.R
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ViewModelScoped
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditProfileViewModel @Inject constructor(
    private val repository: MainRepository,
    private val applicationContext: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {
    private val _userProfileStatus = MutableLiveData<Events<Resource<User>>>()
    val userProfileStatus:LiveData<Events<Resource<User>>> =  _userProfileStatus

    private val _updateProfileStatus = MutableLiveData<Events<Resource<Any>>>()
    val updateProfileStatus:LiveData<Events<Resource<Any>>> = _updateProfileStatus

    private val _curImageUri = MutableLiveData<Uri>()
    val curImageUri: LiveData<Uri> = _curImageUri

    fun setCurrentImageUri(uri: Uri) {
        _curImageUri.postValue(uri)
    }

    fun getUserProfile(uid: String) {
        _userProfileStatus.postValue(Events(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.getUser(uid)
            _userProfileStatus.postValue(Events(result))
        }
    }

    fun updateProfile(updateUser: UpdateUser) {
        if(updateUser.description.isEmpty() || updateUser.userName.isEmpty()){
            val error = "Description or user name field is Empty"
            _updateProfileStatus.postValue(Events(Resource.Error(error)))
        } else if(updateUser.updateCredential.profession.isEmpty()) {
            val error = "Profession Field cannot Be Empty"
            _updateProfileStatus.postValue(Events(Resource.Error(error)))
        } else if(updateUser.updateCredential.interest.isEmpty()) {
            val error = "Interest can't Be Empty"
            _updateProfileStatus.postValue((Events(Resource.Error(error))))
        } else {
            _updateProfileStatus.postValue(Events(Resource.Loading()))
            viewModelScope.launch(dispatcher) {
                val result = repository.updateProfile(updateUser)
                _updateProfileStatus.postValue(Events(result))
            }
        }
    }
}