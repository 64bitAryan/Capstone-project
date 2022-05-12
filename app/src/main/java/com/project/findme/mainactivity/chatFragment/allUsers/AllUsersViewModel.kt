package com.project.findme.mainactivity.chatFragment.allUsers

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
class AllUsersViewModel @Inject constructor(
    private val repository: MainRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
):ViewModel() {
    private val _getAllUsersStatus = MutableLiveData<Events<Resource<List<User>>>>()
    val getAllUsersStatus: LiveData<Events<Resource<List<User>>>> = _getAllUsersStatus

    fun getUsers(uid: String, type:String){
        _getAllUsersStatus.postValue(Events(Resource.Loading()))
        viewModelScope.launch(dispatcher){
            val result = repository.getUsers(uid, type)
            _getAllUsersStatus.postValue(Events(result))
        }
    }
}