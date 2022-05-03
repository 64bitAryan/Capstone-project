package com.project.findme.mainactivity.chatFragment.chatWindowFragment

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.findme.data.entity.Message
import com.project.findme.mainactivity.chatFragment.repository.ChatRepository
import com.project.findme.utils.Events
import com.project.findme.utils.FirebaseCallback
import com.project.findme.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChatWindowViewModel @Inject constructor(
    private val repository: ChatRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
):ViewModel() {
    private val _sendMessageStatus = MutableLiveData<Events<Resource<Any>>>()
    val sendMessageStatus: LiveData<Events<Resource<Any>>> = _sendMessageStatus

    private val _listenUserMessagesStatus = MutableLiveData<Events<Resource<List<Message?>>>>()
    val listenUserMessagesStatus: LiveData<Events<Resource<List<Message?>>>> = _listenUserMessagesStatus


    fun sendMessage(message: Message){
        if(message.toId.isNotEmpty() && message.message.isNotEmpty()){
            _sendMessageStatus.postValue(Events(Resource.Loading()))
            viewModelScope.launch (dispatcher){
                val result = repository.sendMessage(message)
                _sendMessageStatus.postValue(Events(result))
            }
        }
    }

    fun listenMessage(fromId:String, toId:String){
        _listenUserMessagesStatus.postValue(Events(Resource.Loading()))
        viewModelScope.launch (dispatcher){
            repository.listenForMessages(fromId, toId,object: FirebaseCallback{
                override fun onCallBack(messages: List<Message?>) {
                    _listenUserMessagesStatus.postValue(Events(Resource.Success(messages)))
                }
            })
        }
    }
}