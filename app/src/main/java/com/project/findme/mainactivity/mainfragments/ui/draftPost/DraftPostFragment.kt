package com.project.findme.mainactivity.mainfragments.ui.draftPost

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.project.findme.adapter.DraftPostAdapter
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentDraftpostScreenBinding.bind(view)

        setUpRecyclerView(binding)
        subscribeToObserve(binding)

        FirebaseAuth.getInstance().currentUser?.let { viewModel.getDraftPosts(it.uid) }
    }

    private fun subscribeToObserve(binding: FragmentDraftpostScreenBinding) {
        viewModel.draftPostStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.draftProgressbar.isVisible = false
                snackbar(it)
                Log.d("TAGDRAFTPOST", "subscribeToObserve: $it")
            },
            onLoading = {
                binding.draftProgressbar.isVisible = true
                draftAdapter.posts = listOf()
            }
        ) { posts ->
            draftAdapter.posts = posts
            binding.draftProgressbar.isVisible = false
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
    }
}