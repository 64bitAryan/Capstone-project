package com.project.findme.mainactivity.chatFragment.repository

import android.util.Log
import com.google.firebase.database.*
import com.project.findme.data.entity.Message
import com.project.findme.data.entity.Post
import com.project.findme.utils.FirebaseCallback
import com.project.findme.utils.Resource
import com.project.findme.utils.safeCall
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.withContext


class DefaultChatRepository(): ChatRepository {

    override suspend fun sendMessage(message:Message) = withContext(Dispatchers.IO) {
        safeCall {
            val ref = FirebaseDatabase.getInstance()
                .getReference("/user-messages/${message.fromId}/${message.toId}")
                .push()
            val toRef = FirebaseDatabase.getInstance()
                .getReference("/user-messages/${message.toId}/${message.fromId}")
                .push()
            ref.setValue(message)
            toRef.setValue(message)
            Resource.Success(Any())
        }
    }

    override suspend fun listenForMessages(fromId:String, toId:String, firebaseCallback: FirebaseCallback) = withContext(Dispatchers.IO){

        safeCall {
            val messages:MutableList<Message?> =  mutableListOf()
            val ref = FirebaseDatabase.getInstance()
                .getReference("/user-messages/${fromId}/${toId}")

            ref.addChildEventListener(object: ChildEventListener{
                override fun onChildAdded(p0: DataSnapshot, previousChildName: String?) {

                    Log.d("DefaultChatRepo: ",p0.getValue(Message::class.java).toString())
                    p0.getValue(Message::class.java).also { message ->
                        messages.add(message)
                        Log.d("DefaultChatRepository in repo", messages.toString())
                    }
                    firebaseCallback.onCallBack(messages.toList())
                }

                override fun onCancelled(error: DatabaseError) { }
                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) { }
                override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) { }
                override fun onChildRemoved(snapshot: DataSnapshot) { }

            })

            Log.d("DefaultChatRepository in repo", messages.toString())
            Resource.Success(messages.toList())
        }
    }
}