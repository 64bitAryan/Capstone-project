package com.project.findme.mainactivity.mainfragments.ui.listFollowers

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.project.findme.adapter.ListAdapter
import com.project.findme.utils.Constants
import com.project.findme.utils.EventObserver
import com.project.findme.utils.snackbar
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentSuggestionListBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SuggestionListFragment : Fragment(R.layout.fragment_suggestion_list) {
    @Inject
    lateinit var listAdapter: ListAdapter

    val viewModel: ListFollowersViewModel by viewModels()
    private lateinit var binding: FragmentSuggestionListBinding
    private lateinit var uid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.takeIf { it.containsKey(Constants.FRAGMENT_ARG_KEY) }?.apply {
            uid = getString(Constants.FRAGMENT_ARG_KEY).toString()
            viewModel.getSuggestionList(uid)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObserve()

        binding = FragmentSuggestionListBinding.bind(view)

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
            swipeRefreshLayoutSuggestion.setOnRefreshListener {
                viewModel.getSuggestionList(uid)
            }
        }

    }

    private fun setUpRecyclerView() {
        binding.rvListSuggestions.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = listAdapter
        }
    }

    private fun subscribeToObserve() {
        viewModel.list.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.swipeRefreshLayoutSuggestion.isRefreshing = false
                snackbar(it)
            },
            onLoading = {
                listAdapter.users = listOf()
                binding.swipeRefreshLayoutSuggestion.isRefreshing = true
            }
        ) { users ->
            listAdapter.users = users
            binding.swipeRefreshLayoutSuggestion.isRefreshing = false
        })

        viewModel.follow.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.swipeRefreshLayoutSuggestion.isRefreshing = false
                snackbar(it)
            },
            onLoading = {
                binding.swipeRefreshLayoutSuggestion.isRefreshing = true
            }
        ) {
            viewModel.getSuggestionList(uid)
            binding.swipeRefreshLayoutSuggestion.isRefreshing = false
        })
    }
}