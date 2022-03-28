package com.project.findme.mainactivity.mainfragments.ui.comment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.findme.data.entity.Comment
import com.project.findme.mainactivity.repository.MainRepository
import com.project.findme.utils.Events
import com.project.findme.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CommentViewModel @Inject constructor(
    private val repository: MainRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) :ViewModel() {
    private val _createCommentStatus = MutableLiveData<Events<Resource<Comment>>>()
    val createCommentStatus:LiveData<Events<Resource<Comment>>> = _createCommentStatus

    private val _getCommentStatus = MutableLiveData<Events<Resource<List<Comment>>>>()
    val getCommentsForPostStatus: LiveData<Events<Resource<List<Comment>>>> = _getCommentStatus

    fun createComment(commentText:String, postId: String) {
        if(commentText.isNotEmpty()) {
            _createCommentStatus.postValue(Events(Resource.Loading()))
            viewModelScope.launch(dispatcher) {
                val result = repository.createComment(commentText, postId)
                _createCommentStatus.postValue(Events(result))
            }
        }
    }

    fun getCommentForPost(postId: String) {
        _getCommentStatus.postValue(Events(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.getCommentFromPost(postId)
            _getCommentStatus.postValue(Events(result))
        }
    }
}