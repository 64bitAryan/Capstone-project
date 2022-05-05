package com.project.findme.mainactivity.mainfragments.ui.draftPost

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.project.findme.adapter.DraftPostAdapter
import com.project.findme.data.entity.Post
import com.project.findme.utils.EventObserver
import com.project.findme.utils.snackbar
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentDraftpostScreenBinding
import com.ryan.findme.databinding.FragmentSearchPersonBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class DraftPostFragment : Fragment(R.layout.fragment_draftpost_screen) {

    @Inject
    lateinit var draftAdapter: DraftPostAdapter
    val viewModel: DraftPostViewModel by viewModels()
    private lateinit var binding: FragmentDraftpostScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseAuth.getInstance().currentUser?.let { viewModel.getDraftPosts(it.uid) }

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDraftpostScreenBinding.bind(view)

        setUpRecyclerView(binding)
        subscribeToObserve(binding)

        binding.swipeRefreshLayoutDraft.setOnRefreshListener {
            FirebaseAuth.getInstance().currentUser?.let { viewModel.getDraftPosts(it.uid) }
        }

    }

    private fun showConfirmationDialog(post: Post) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Post")
            .setMessage("Are you sure you want to delete this draft?")
            .setPositiveButton(
                "Yes"
            ) { _, _ ->
                viewModel.deleteDraftPost(post)
            }
            .setNegativeButton("No", null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    private fun subscribeToObserve(binding: FragmentDraftpostScreenBinding) {
        viewModel.draftPostStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.swipeRefreshLayoutDraft.isRefreshing = false
                snackbar(it)
            },
            onLoading = {
                binding.swipeRefreshLayoutDraft.isRefreshing = true
                draftAdapter.posts = listOf()
            }
        ) { posts ->
            draftAdapter.posts = posts
            binding.swipeRefreshLayoutDraft.isRefreshing = false
        })

        viewModel.deletePostStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.swipeRefreshLayoutDraft.isRefreshing = false
                snackbar(it)
            },
            onLoading = {
                binding.swipeRefreshLayoutDraft.isRefreshing = true
            }
        ) { deletedPost ->
            draftAdapter.posts -= deletedPost
            binding.swipeRefreshLayoutDraft.isRefreshing = false
        })
    }

    fun setUpRecyclerView(binding: FragmentDraftpostScreenBinding) {
        binding.recyclerViewSearchList.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = draftAdapter
            itemAnimator = null
        }
        draftAdapter.setOnPostClickListener { post ->
            findNavController().navigate(
                DraftPostFragmentDirections.actionDraftPostFragmentToCreatePostFragment(
                    imageUrl = post.imageUrl,
                    title = post.title,
                    description = post.text,
                    postId = post.id
                )
            )
        }
        draftAdapter.setOnDeleteClickListener { post ->
            showConfirmationDialog(post)
        }
    }
}