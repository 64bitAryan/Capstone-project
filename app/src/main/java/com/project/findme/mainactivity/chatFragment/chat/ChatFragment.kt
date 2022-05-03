package com.project.findme.mainactivity.chatFragment.chat

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.project.findme.adapter.UserAdapter
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentChatScreenBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ChatFragment : Fragment(R.layout.fragment_chat_screen){
    @Inject
    lateinit var userAdapter: UserAdapter
    lateinit var binding: FragmentChatScreenBinding
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentChatScreenBinding.bind(view)
        binding.apply {
            addChatFb.setOnClickListener {
                findNavController().navigate(R.id.action_chatFragment_to_allUsers)
            }
        }
        setupRecyclerView()
    }
    fun setupRecyclerView(){
        binding.chatRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userAdapter
            itemAnimator = null
        }
        binding.apply {
            chatRv.addOnScrollListener(object: RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if(dy > 10 && addChatFb.isShown){
                        addChatFb.hide()
                    }
                    if(dy < -10 && !addChatFb.isShown){
                        addChatFb.show()
                    }
                    if (!recyclerView.canScrollVertically(-1)) {
                        addChatFb.show()
                    }
                }
            })
        }
    }
}