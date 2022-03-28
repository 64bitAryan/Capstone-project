package com.project.findme.mainactivity.mainfragments.ui.listFollowers

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
class ListFollowersViewModel @Inject constructor(
    private val repository: MainRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {
    private val _list = MutableLiveData<Events<Resource<List<User>>>>()
    val list: LiveData<Events<Resource<List<User>>>> = _list

    fun getUsers(uid: String, type: String){
        _list.postValue(Events(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.getUsers(uid, type)
            _list.postValue(Events(result))
        }
    }
}