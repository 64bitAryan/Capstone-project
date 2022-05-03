package com.project.findme.mainactivity.mainfragments.ui.createPost

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class CreatePostViewModel @Inject constructor(
    private val repository: MainRepository,
    private val applicationContext: Context,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val _createPostStatus = MutableLiveData<Events<Resource<Any>>>()
    val createPostStatus: LiveData<Events<Resource<Any>>> = _createPostStatus

    private val _curImageUri = MutableLiveData<Uri>()
    val curImageUri: LiveData<Uri> = _curImageUri

    fun setCurrentImageUri(uri: Uri) {
        _curImageUri.postValue(uri)
    }

    fun createPost(imageUri: Uri, title: String, description: String, postId: String) {
        if (title.isEmpty() || description.isEmpty()) {
            val error = applicationContext.getString(R.string.error_input_empty)
            _createPostStatus.postValue(Events(Resource.Error(error)))
        } else if (imageUri == Uri.EMPTY) {
            val error = "Pleas Select an Image"
            _createPostStatus.postValue(Events(Resource.Error(error)))
        } else {
            viewModelScope.launch(Dispatchers.IO) {
                _createPostStatus.postValue(Events(Resource.Loading()))
                val result = repository.createPost(imageUri, title, description, postId)
                _createPostStatus.postValue(Events(result))
            }
        }
    }

    fun createDraftPost(imageUri: Uri, title: String, description: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _createPostStatus.postValue(Events(Resource.Loading()))
            val result = repository.createDraftPost(imageUri, title, description)
            _createPostStatus.postValue(Events(result))
        }
    }

    fun updateDraftPost(
        imageUri: Uri,
        title: String,
        description: String,
        postId: String,
        imageUrl: String
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            _createPostStatus.postValue(Events(Resource.Loading()))
            val result = repository.updateDraftPost(imageUri, title, description, postId, imageUrl)
            _createPostStatus.postValue(Events(result))
        }
    }
}