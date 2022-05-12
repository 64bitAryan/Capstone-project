package com.project.findme.mainactivity.chatFragment.allUsers

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.project.findme.adapter.UserAdapter
import com.project.findme.utils.EventObserver
import com.project.findme.utils.snackbar
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentAllusersBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AllUsersFragment : Fragment(R.layout.fragment_allusers) {
    @Inject
    lateinit var userAdapter: UserAdapter
    val viewModel: AllUsersViewModel by viewModels()
    private lateinit var binding: FragmentAllusersBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentAllusersBinding.bind(view)
        FirebaseAuth.getInstance().currentUser?.let { viewModel.getUsers(it.uid) }
        setupRecyclerView()
        subscribeToObserver()
    }

    private fun subscribeToObserver() {
        viewModel.getAllUsersStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.allusersPb.isVisible = false
                snackbar(it)
            },
            onLoading = {
                userAdapter.users = listOf()
                binding.allusersPb.isVisible = true
            }
        ) {
            binding.allusersPb.isVisible = false
            userAdapter.users = it
        })
    }

    private fun setupRecyclerView() {
        binding.adduserRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = userAdapter
            itemAnimator = null
        }
        userAdapter.setOnUserClickListener { user ->
            Log.d("AllUsersFragment: ", user.toString())
            val action = AllUsersFragmentDirections.actionAllUsersToChatWindowFragment(
                user.uid,
                user.userName
            )
            findNavController().navigate(action)
        }
    }
}