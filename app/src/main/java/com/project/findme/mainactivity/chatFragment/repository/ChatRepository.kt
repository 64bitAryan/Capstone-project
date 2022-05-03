package com.project.findme.mainactivity.chatFragment.repository

import com.project.findme.data.entity.Message
import com.project.findme.utils.FirebaseCallback
import com.project.findme.utils.Resource

interface ChatRepository {
    suspend fun sendMessage(message:Message): Resource<Any>

    suspend fun listenForMessages(fromId:String, toId:String, firebaseCallback: FirebaseCallback): Resource<List<Message?>>
}