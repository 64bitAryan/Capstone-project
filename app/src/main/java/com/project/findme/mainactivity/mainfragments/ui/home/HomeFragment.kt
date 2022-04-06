package com.project.findme.mainactivity.mainfragments.ui.home

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.project.findme.adapter.PostAdapter
import com.project.findme.mainactivity.mainfragments.ui.comment.CommentFragmentArgs
import com.project.findme.utils.EventObserver
import com.project.findme.utils.snackbar
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentHomeScreenBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home_screen) {
    @Inject
    lateinit var postAdapter: PostAdapter
    val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeScreenBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeScreenBinding.bind(view)
        FirebaseAuth.getInstance().currentUser?.let { viewModel.getPost(it.uid) }

        setUpRecyclerView()
        subscribeToObserver()

        binding.apply {
            createPostFb.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_createPostFragment)
            }

            swipeRefreshLayout.setOnRefreshListener {
                FirebaseAuth.getInstance().currentUser?.let { viewModel.getPost(it.uid) }
            }
        }
    }

    private fun subscribeToObserver() {
        viewModel.post.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.swipeRefreshLayout.isRefreshing = false
                snackbar(it)
            },
            onLoading = {
                binding.swipeRefreshLayout.isRefreshing = true
                postAdapter.posts = listOf()
            }
        ) { postList ->
            postAdapter.posts = postList
            binding.swipeRefreshLayout.isRefreshing = false
        })

        viewModel.deletePostStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.swipeRefreshLayout.isRefreshing = false
                snackbar(it)
            },
            onLoading = {
                binding.swipeRefreshLayout.isRefreshing = true
            }
        ) { deletedPost ->
            postAdapter.posts -= deletedPost
            binding.swipeRefreshLayout.isRefreshing = false
        })

        viewModel.like.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.swipeRefreshLayout.isRefreshing = false
                snackbar(it)
            },
            onLoading = {
                binding.swipeRefreshLayout.isRefreshing = true
            }
        ) {
            FirebaseAuth.getInstance().currentUser?.let { viewModel.getPost(it.uid) }
            binding.swipeRefreshLayout.isRefreshing = false
        })
    }

    private fun setUpRecyclerView() {

        postAdapter.setOnCommentClickListener { post ->
            findNavController().navigate(
                R.id.action_homeFragment_to_commentFragment,
                Bundle().apply {
                    putString("postId", post.id)
                }
            )
        }

        postAdapter.setOnDeleteClickListener { post ->
            viewModel.deletePost(post)
        }

        postAdapter.setOnLikeClickListener { post ->
            post.isLiking = true
            viewModel.likePost(post)
        }

        binding.postRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
        }
    }
}