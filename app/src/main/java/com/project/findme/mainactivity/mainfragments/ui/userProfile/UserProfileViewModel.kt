package com.project.findme.mainactivity.mainfragments.ui.userProfile

import android.content.Context
import androidx.lifecycle.*
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
    private val state: SavedStateHandle,
    private val repository: MainRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main
) : ViewModel() {
    private val _userProfileStatus = MutableLiveData<Events<Resource<User>>>()
    val userProfileStatus: LiveData<Events<Resource<User>>> = _userProfileStatus

}