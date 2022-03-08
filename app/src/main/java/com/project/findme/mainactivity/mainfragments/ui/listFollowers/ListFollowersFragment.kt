package com.project.findme.mainactivity.mainfragments.ui.listFollowers

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.google.firebase.auth.FirebaseAuth
import com.project.findme.adapter.ListAdapter
import com.project.findme.utils.EventObserver
import com.project.findme.utils.snackbar
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentListsFollowersBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ListFollowersFragment : Fragment(R.layout.fragment_lists_followers) {

    @Inject
    lateinit var listAdapter: ListAdapter

    @Inject
    lateinit var glide: RequestManager
    private lateinit var viewModel: ListFollowersViewModel
    private lateinit var binding: FragmentListsFollowersBinding
    private val args: ListFollowersFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[ListFollowersViewModel::class.java]
        subscribeToObserve()

        binding = FragmentListsFollowersBinding.bind(view)

        viewModel.getUsers(args.uid, args.type)

        when (args.type) {
            "Followers" -> {
                binding.btnFollowersList.setBackgroundResource(R.drawable.button_bg)
                binding.btnFollowingsList.setBackgroundResource(0)
                binding.btnMutualList.setBackgroundResource(0)
            }
            "Followings" -> {
                binding.btnFollowingsList.setBackgroundResource(R.drawable.button_bg)
                binding.btnFollowersList.setBackgroundResource(0)
                binding.btnMutualList.setBackgroundResource(0)
            }
        }

        setUpRecyclerView()

        listAdapter.setOnFollowClickListener {
            viewModel.followUser(it)
        }

        listAdapter.setOnUnFollowClickListener {
            viewModel.unFollowUser(it)
        }

        binding.apply {
            btnFollowersList.setOnClickListener {
                viewModel.getUsers(args.uid, "Followers")
                it.setBackgroundResource(R.drawable.button_bg)
                btnFollowingsList.setBackgroundResource(0)
                btnMutualList.setBackgroundResource(0)
            }
            btnFollowingsList.setOnClickListener {
                viewModel.getUsers(args.uid, "Followings")
                it.setBackgroundResource(R.drawable.button_bg)
                btnFollowersList.setBackgroundResource(0)
                btnMutualList.setBackgroundResource(0)
            }
            btnMutualList.setOnClickListener {
                viewModel.getUsers(args.uid, "mutual")
                it.setBackgroundResource(R.drawable.button_bg)
                btnFollowersList.setBackgroundResource(0)
                btnFollowingsList.setBackgroundResource(0)
            }
        }
    }

    private fun setUpRecyclerView() {
        binding.rvListFollowers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = listAdapter
        }
    }

    private fun subscribeToObserve() {
        viewModel.list.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.progressBarLists.isVisible = false
                snackbar(it)
            },
            onLoading = {
                listAdapter.users = listOf()
                binding.progressBarLists.isVisible = true
            }
        ) { users ->
            listAdapter.users = users
            binding.progressBarLists.isVisible = false
        })

        viewModel.follow.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.progressBarLists.isVisible = false
                snackbar(it)
            },
            onLoading = {
                binding.progressBarLists.isVisible = true
            }
        ) { user ->
            viewModel.getUsers(user.uid, "Followers")
            binding.progressBarLists.isVisible = false
        })
    }
}