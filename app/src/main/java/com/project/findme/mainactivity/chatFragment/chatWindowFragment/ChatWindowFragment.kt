package com.project.findme.mainactivity.chatFragment.chatWindowFragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide.init
import com.google.firebase.auth.FirebaseAuth
import com.project.findme.adapter.MessageAdapter
import com.project.findme.adapter.UserAdapter
import com.project.findme.data.entity.Message
import com.project.findme.utils.EventObserver
import com.project.findme.utils.FirebaseCallback
import com.project.findme.utils.hideKeyboard
import com.project.findme.utils.snackbar
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentChatwindowBinding
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class ChatWindowFragment:Fragment(R.layout.fragment_chatwindow) {
    @Inject
    lateinit var messageAdapter: MessageAdapter
    private lateinit var binding: FragmentChatwindowBinding
    private val viewModel: ChatWindowViewModel by viewModels()
    private val args by navArgs<ChatWindowFragmentArgs>()
    private val auth = FirebaseAuth.getInstance()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.listenMessage(auth.currentUser!!.uid, args.currentUser)

        binding = FragmentChatwindowBinding.bind(view)

        binding.apply {
            val uuid = UUID.randomUUID().toString().replace("-", "")
            sendMessageBt.setOnClickListener {
                val textMessage = commentTextInputEt.text.toString()
                val message = Message(
                    toId = args.currentUser,
                    message = textMessage,
                    fromId = auth.currentUser!!.uid,
                    timeStamp = System.currentTimeMillis(),
                    id = uuid
                )
                viewModel.sendMessage(message)
            }
        }
        setupRecyclerView()
        subscribeToObserver()
    }

    private fun setupRecyclerView() {
        binding.chatWindowRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = messageAdapter
        }
    }

    private fun subscribeToObserver(){
        viewModel.sendMessageStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                snackbar(it)
            },
            onLoading = {
            }
        ){
            binding.commentTextInputEt.setText("")
            hideKeyboard(requireActivity())
        })

        viewModel.listenUserMessagesStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.chatWindowPb.isVisible = false
                snackbar(it)
            },
            onLoading = {
                binding.chatWindowPb.isVisible = true
            }
        ){ messages ->
            binding.chatWindowPb.isVisible = false
            messageAdapter.messages = messages
        })
    }
}