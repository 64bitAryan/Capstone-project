package com.project.findme.mainactivity.mainfragments.ui.searchedProfile

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
class SearchedProfileViewModel @Inject constructor(
    private val repository: MainRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val _searchedProfileStatus = MutableLiveData<Events<Resource<User>>>()
    val searchedProfileStatus: LiveData<Events<Resource<User>>> = _searchedProfileStatus

    fun updateUI(uid: String) {
        _searchedProfileStatus.postValue(Events(Resource.Loading()))

        viewModelScope.launch(dispatcher) {
            val user = repository.updateProfileUI(uid)
            _searchedProfileStatus.postValue(Events(user))
        }
    }

    private val _post = MutableLiveData<Events<Resource<List<Post>>>>()
    val post: LiveData<Events<Resource<List<Post>>>> = _post

    fun getPost(uid: String) {
        _post.postValue(Events(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.getPostForProfile(uid)
            _post.postValue(Events(result))
        }
    }

    fun followUser(uid: String){
        _searchedProfileStatus.postValue(Events(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val user = repository.followUser(uid)
            _searchedProfileStatus.postValue(Events(user))
        }
    }

    fun unFollowUser(uid: String){
        _searchedProfileStatus.postValue(Events(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val user = repository.unFollowUser(uid)
            _searchedProfileStatus.postValue(Events(user))
        }
    }
}