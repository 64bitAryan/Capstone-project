package com.project.findme.mainactivity.mainfragments.ui.mutualsList

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.project.findme.adapter.ListAdapter
import com.project.findme.mainactivity.mainfragments.ui.listFollowers.ListFollowersFragmentDirections
import com.project.findme.mainactivity.mainfragments.ui.listFollowersUser.ListFollowersFragmentUserDirections
import com.project.findme.utils.Constants
import com.project.findme.utils.Constants.FRAGMENT_ARG_KEY
import com.project.findme.utils.EventObserver
import com.project.findme.utils.snackbar
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentMutualListBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MutualsListFragment : Fragment(R.layout.fragment_mutual_list) {
    @Inject
    lateinit var listAdapter: ListAdapter

    val viewModel: MutualsListViewModel by viewModels()
    private lateinit var binding: FragmentMutualListBinding
    private lateinit var uid: String
    private lateinit var args: List<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.takeIf { it.containsKey(FRAGMENT_ARG_KEY) }?.apply {
            args = getStringArrayList(Constants.FRAGMENT_ARG_KEY)!!.toList()
            uid = args[0]
            viewModel.getMutualList(uid)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObserve()

        binding = FragmentMutualListBinding.bind(view)

        setUpRecyclerView()

        listAdapter.setOnFollowClickListener {
            viewModel.followUser(it)
        }

        listAdapter.setOnUnFollowClickListener {
            viewModel.unFollowUser(it)
        }

        listAdapter.setOnUserClickListener { user ->
            if (args[1] == "ListFollowers") {
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
            } else {
                if (FirebaseAuth.getInstance().currentUser?.uid == user.uid) {
                    findNavController().navigate(
                        ListFollowersFragmentUserDirections.actionListFollowersFragmentUserToUserProfileFragment2()
                    )
                } else {
                    findNavController().navigate(
                        ListFollowersFragmentUserDirections.actionListFollowersFragmentUserToSearchedProfileFragment(
                            uid = user.uid,
                            username = user.userName
                        )
                    )
                }
            }

        }

        binding.apply {
            swipeRefreshLayoutMutuals.setOnRefreshListener {
                viewModel.getMutualList(uid)
            }
        }
    }

    private fun setUpRecyclerView() {
        binding.rvListMutuals.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = listAdapter
        }
    }

    private fun subscribeToObserve() {
        viewModel.list.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.swipeRefreshLayoutMutuals.isRefreshing = false
                snackbar(it)
            },
            onLoading = {
                listAdapter.users = listOf()
                binding.swipeRefreshLayoutMutuals.isRefreshing = true
            }
        ) { users ->
            listAdapter.users = users
            binding.swipeRefreshLayoutMutuals.isRefreshing = false
        })

        viewModel.follow.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.swipeRefreshLayoutMutuals.isRefreshing = false
                snackbar(it)
            },
            onLoading = {
                binding.swipeRefreshLayoutMutuals.isRefreshing = true
            }
        ) {
            viewModel.getMutualList(uid)
            binding.swipeRefreshLayoutMutuals.isRefreshing = false
        })
    }
}