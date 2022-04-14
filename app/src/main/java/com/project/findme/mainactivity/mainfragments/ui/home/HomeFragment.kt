package com.project.findme.mainactivity.mainfragments.ui.home

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.project.findme.adapter.PostAdapter
import com.project.findme.data.entity.Comment
import com.project.findme.data.entity.Post
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
    var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseAuth.getInstance().currentUser?.let { viewModel.getPost(it.uid) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeScreenBinding.bind(view)

        subscribeToObserver()
        setUpRecyclerView()

        binding.apply {
            createPostFb.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_createPostFragment)
            }

            swipeRefreshLayout.setOnRefreshListener {
                FirebaseAuth.getInstance().currentUser?.let { viewModel.getPost(it.uid) }
            }
            postRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 10 && createPostFb.isShown) {
                        createPostFb.hide()
                    }
                    if (dy < -10 && !createPostFb.isShown) {
                        createPostFb.show()
                    }
                    if (!recyclerView.canScrollVertically(-1)) {
                        createPostFb.show()
                    }
                }
            })
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
            postAdapter.posts[index].isLiking = false
            if (postAdapter.posts[index].isLiked) postAdapter.posts[index].likedBy.remove(
                FirebaseAuth.getInstance().currentUser?.uid
            ) else postAdapter.posts[index].likedBy.add(
                FirebaseAuth.getInstance().currentUser!!.uid
            )
            postAdapter.posts[index].isLiked = !postAdapter.posts[index].isLiked
            postAdapter.notifyItemChanged(index)
            binding.swipeRefreshLayout.isRefreshing = false
        })
    }

    private fun showConfirmationDialog(post: Post) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Post")
            .setMessage("Are you sure you want to delete this post?")
            .setPositiveButton(
                "Yes"
            ) { _, _ ->
                viewModel.deletePost(post)
            }
            .setNegativeButton("No", null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
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
            showConfirmationDialog(post)
        }

        postAdapter.setOnLikeClickListener { post, i ->
            post.isLiking = true
            index = i
            viewModel.likePost(post)
        }

        binding.postRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
        }
    }
}