package com.project.findme.mainactivity.mainfragments.ui.likedby

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.project.findme.adapter.ListAdapter
import com.project.findme.mainactivity.mainfragments.ui.listFollowers.ListFollowersFragmentDirections
import com.project.findme.utils.EventObserver
import com.project.findme.utils.snackbar
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentFollowersListBinding
import com.ryan.findme.databinding.FragmentLikedByBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LikedByFragment : Fragment(R.layout.fragment_liked_by) {

    @Inject
    lateinit var listAdapter: ListAdapter
    val viewModel: LikedByViewModel by viewModels()
    private val args: LikedByFragmentArgs by navArgs()
    private lateinit var binding: FragmentLikedByBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel.getUsersLiked(args.postId)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        subscribeToObserve()

        binding = FragmentLikedByBinding.bind(view)

        setUpRecyclerView()

    }

    private fun setUpRecyclerView() {
        binding.rvListLikedby.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = listAdapter
        }
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
    }

    private fun subscribeToObserve() {
        viewModel.list.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.progressBarLikedby.isVisible = false
                snackbar(it)
            },
            onLoading = {
                listAdapter.users = listOf()
                binding.progressBarLikedby.isVisible = true
            }
        ) { users ->
            listAdapter.users = users
            binding.progressBarLikedby.isVisible = false
        })

        viewModel.follow.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.progressBarLikedby.isVisible = false
                snackbar(it)
            },
            onLoading = {
                binding.progressBarLikedby.isVisible = true
            }
        ) {
            viewModel.getUsersLiked(args.postId)
            binding.progressBarLikedby.isVisible = false
        })
    }

}