package com.project.findme.authactivity.authfragments.ui.register

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(private val state: SavedStateHandle) :
    ViewModel() {

    var email = state.get<String>("email") ?: ""
        set(value) {
            field = value
            state.set("email", value)
        }

    var password = state.get<String>("password") ?: ""
        set(value) {
            field = value
            state.set("password", value)
        }

    var uname = state.get<String>("uname") ?: ""
        set(value) {
            field = value
            state.set("uname", value)
        }

    var cpassword = state.get<String>("cpassword") ?: ""
        set(value) {
            field = value
            state.set("cpassword", value)
        }
}