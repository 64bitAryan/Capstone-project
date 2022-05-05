package com.project.findme.mainactivity.mainfragments.ui.mutualsList

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
class MutualsListViewModel @Inject constructor(
    private val repository: MainRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {

    private val _list = MutableLiveData<Events<Resource<List<User>>>>()
    val list: LiveData<Events<Resource<List<User>>>> = _list

    fun getMutualList(uid: String) {
        _list.postValue(Events(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.getMutualList(uid)
            _list.postValue(Events(result))
        }
    }

    private val _follow = MutableLiveData<Events<Resource<User>>>()
    val follow: LiveData<Events<Resource<User>>> = _follow

    fun followUser(uid: String) {
        _follow.postValue(Events(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val user = repository.followUser(uid)
            _follow.postValue(Events(user))
        }
    }

    fun unFollowUser(uid: String) {
        _follow.postValue(Events(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val user = repository.unFollowUser(uid)
            _follow.postValue(Events(user))
        }
    }
}