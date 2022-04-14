package com.project.findme.mainactivity.chatFragment.chatWindowFragment

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentChatwindowBinding

class ChatWindowFragment:Fragment(R.layout.fragment_chatwindow) {
    private lateinit var binding: FragmentChatwindowBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChatwindowBinding.bind(view)

    }
}