package com.project.findme.mainactivity.mainfragments.ui.home

import android.content.Context
import android.media.metrics.Event
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.findme.data.entity.Post
import com.project.findme.mainactivity.repository.MainRepository
import com.project.findme.utils.Events
import com.project.findme.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: MainRepository,
    private val dispatcher:CoroutineDispatcher = Dispatchers.Main
):ViewModel() {
    private val _post = MutableLiveData<Events<Resource<List<Post>>>>()
    val post: LiveData<Events<Resource<List<Post>>>> = _post

    private val _deletePostStatus = MutableLiveData<Events<Resource<Post>>>()
    val deletePostStatus: LiveData<Events<Resource<Post>>> = _deletePostStatus

    private val _getFollowerPostStatus = MutableLiveData<Events<Resource<List<Post>>>>()
    val getFollowersPostStatus:LiveData<Events<Resource<List<Post>>>> = _getFollowerPostStatus

    fun getPost(uid: String) {
        _post.postValue(Events(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.getPostForProfile(uid)
            _post.postValue(Events(result))
        }
    }

    fun deletePost(post: Post) {
        _deletePostStatus.postValue(Events(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.deletePost(post)
            _deletePostStatus.postValue(Events(result))
        }
    }

    fun getPostFromFollower() {
        _getFollowerPostStatus.postValue(Events(Resource.Loading()))
        viewModelScope.launch (dispatcher){
            val result = repository.getPostForFollows()
            _getFollowerPostStatus.postValue(Events(result))
        }
    }
}