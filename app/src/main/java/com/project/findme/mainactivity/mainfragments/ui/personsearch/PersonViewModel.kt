package com.project.findme.mainactivity.mainfragments.ui.personsearch

import androidx.lifecycle.*
import com.project.findme.authactivity.repositories.AuthRepository
import com.project.findme.data.entity.User
import com.project.findme.utils.Events
import com.project.findme.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PersonViewModel @Inject constructor(
    private val repository: AuthRepository,
    private val dispatcher: CoroutineDispatcher = Dispatchers.Main,
    private val state: SavedStateHandle
) : ViewModel() {

    var searchQuery = state.get<String>("searchQuery") ?: ""
        set(value) {
            field = value
            state.set("searchQuery", value)
        }

    private val _searchPersonStatus = MutableLiveData<Events<Resource<List<User>>>>()
    val searchPersonStatus: LiveData<Events<Resource<List<User>>>> = _searchPersonStatus

    fun searchPerson() {
        _searchPersonStatus.postValue(Events(Resource.Loading()))
        viewModelScope.launch(dispatcher) {
            val result = repository.searchUser(searchQuery.toString())
            _searchPersonStatus.postValue(Events(result))
        }
    }

}