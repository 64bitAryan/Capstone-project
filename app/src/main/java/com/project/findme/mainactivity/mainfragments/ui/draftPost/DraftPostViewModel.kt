package com.project.findme.mainactivity.mainfragments.ui.draftPost

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
class DraftPostViewModel @Inject constructor(
    private val repository: MainRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main,
) : ViewModel() {

    private val _draftPostStatus = MutableLiveData<Events<Resource<List<Post>>>>()
    val draftPostStatus: LiveData<Events<Resource<List<Post>>>> = _draftPostStatus

    private val _deletePostStatus = MutableLiveData<Events<Resource<Post>>>()
    val deletePostStatus: LiveData<Events<Resource<Post>>> = _deletePostStatus

    fun getDraftPosts(uid: String) {
        _draftPostStatus.postValue(Events(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.getDraftPosts(uid)
            _draftPostStatus.postValue(Events(result))
        }
    }

    fun deleteDraftPost(post: Post) {
        _deletePostStatus.postValue(Events(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.deleteDraftPost(post)
            _deletePostStatus.postValue(Events(result))
        }
    }
}