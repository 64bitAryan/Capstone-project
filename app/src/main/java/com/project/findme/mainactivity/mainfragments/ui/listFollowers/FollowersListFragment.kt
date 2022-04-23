package com.project.findme.mainactivity.mainfragments.ui.listFollowers

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.project.findme.adapter.ListAdapter
import com.project.findme.utils.Constants
import com.project.findme.utils.EventObserver
import com.project.findme.utils.snackbar
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentFollowersListBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FollowersListFragment : Fragment(R.layout.fragment_followers_list) {

    @Inject
    lateinit var listAdapter: ListAdapter

    val viewModel: ListFollowersViewModel by viewModels()
    private lateinit var binding: FragmentFollowersListBinding
    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.takeIf { it.containsKey(Constants.FRAGMENT_ARG_KEY) }?.apply {
            uid = getString(Constants.FRAGMENT_ARG_KEY).toString()
            viewModel.getFollowersList(uid)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObserve()

        binding = FragmentFollowersListBinding.bind(view)

        setUpRecyclerView()

        listAdapter.setOnFollowClickListener {
            viewModel.followUser(it)
        }

        listAdapter.setOnUnFollowClickListener {
            viewModel.unFollowUser(it)
        }

        listAdapter.setOnUserClickListener { user ->
            if (FirebaseAuth.getInstance().currentUser?.uid == user.uid) {
                findNavController().navigate(
                    ListFollowersFragmentDirections.actionListFollowersFragmentToUserProfileFragment()
                )
            } else {
                findNavController().navigate(
                    ListFollowersFragmentDirections.actionListFollowersFragmentToSearchedProfileFragment(
                        uid = user.uid,
                        username = user.userName
                    )
                )
            }
        }

        binding.apply {
            swipeRefreshLayoutFollowers.setOnRefreshListener {
                viewModel.getFollowersList(uid)
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
                binding.swipeRefreshLayoutFollowers.isRefreshing = false
                snackbar(it)
            },
            onLoading = {
                listAdapter.users = listOf()
                binding.swipeRefreshLayoutFollowers.isRefreshing = true
            }
        ) { users ->
            listAdapter.users = users
            binding.swipeRefreshLayoutFollowers.isRefreshing = false
        })

        viewModel.follow.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.swipeRefreshLayoutFollowers.isRefreshing = false
                snackbar(it)
            },
            onLoading = {
                binding.swipeRefreshLayoutFollowers.isRefreshing = true
            }
        ) {
            viewModel.getFollowersList(uid)
            binding.swipeRefreshLayoutFollowers.isRefreshing = false
        })
    }
}