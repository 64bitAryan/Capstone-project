package com.project.findme.mainactivity.chatFragment.chat

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentChatScreenBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChatFragment : Fragment(R.layout.fragment_chat_screen){
    lateinit var binding: FragmentChatScreenBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChatScreenBinding.bind(view)
        binding.apply {
            addChatFb.setOnClickListener {
                findNavController().navigate(R.id.action_chatFragment_to_allUsers)
            }
        }
    }
}