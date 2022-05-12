package com.project.findme.utils

import com.project.findme.data.entity.Message

interface FirebaseCallback {
    fun onCallBack(messages: List<Message?>)
}