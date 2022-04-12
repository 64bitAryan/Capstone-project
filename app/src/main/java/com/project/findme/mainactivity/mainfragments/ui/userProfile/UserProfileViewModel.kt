package com.project.findme.mainactivity.mainfragments.ui.userProfile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.findme.data.entity.Post
import com.project.findme.data.entity.User
import com.project.findme.mainactivity.repository.MainRepository
import com.project.findme.utils.Events
import com.project.findme.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserProfileViewModel @Inject constructor(
    private val repository: MainRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val _userProfileStatus = MutableLiveData<Events<Resource<User>>>()
    val userProfileStatus: LiveData<Events<Resource<User>>> = _userProfileStatus

    fun updateUI(uid: String) {
        _userProfileStatus.postValue(Events(Resource.Loading()))

        viewModelScope.launch(dispatcher) {
            val user = repository.updateProfileUI(uid)
            _userProfileStatus.postValue(Events(user))
        }
    }

    private val _post = MutableLiveData<Events<Resource<List<Post>>>>()
    val post: LiveData<Events<Resource<List<Post>>>> = _post

    fun getPost(uid: String) {
        _post.postValue(Events(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.getPostForUser(uid)
            _post.postValue(Events(result))
        }
    }
}