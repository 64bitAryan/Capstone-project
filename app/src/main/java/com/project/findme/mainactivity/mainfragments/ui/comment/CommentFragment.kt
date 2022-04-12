package com.project.findme.mainactivity.mainfragments.ui.comment

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.project.findme.adapter.CommentAdapter
import com.project.findme.utils.EventObserver
import com.project.findme.utils.snackbar
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentCommentBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CommentFragment: Fragment(R.layout.fragment_comment) {

    @Inject
    lateinit var commentAdapter: CommentAdapter
    private lateinit var binding: FragmentCommentBinding
    private val viewModel: CommentViewModel by viewModels()
    private val args: CommentFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCommentBinding.bind(view)
        binding.apply {
            sendCommentBt.setOnClickListener {
                val text = commentTextInputEt.text.toString()
                viewModel.createComment(text, args.postId)
            }
        }
        viewModel.getCommentForPost(args.postId)
        setupRecyclerView()
        subscribeToObserve()
    }

    private fun setupRecyclerView() {
        binding.commentRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = commentAdapter
        }
        commentAdapter.setDeleteListener { comment ->
            viewModel.deleteComment(comment)
        }
    }

     private fun subscribeToObserve() {
        viewModel.createCommentStatus.observe(viewLifecycleOwner, EventObserver(
            onError = { error ->
                binding.apply {
                    commentPb.isVisible = false
                    sendCommentBt.isClickable = true
                    snackbar(error)
                }
            },
            onLoading = {
                binding.apply {
                    commentPb.isVisible = true
                    sendCommentBt.isClickable = false
                }
            }
        ){
            binding.apply {
                commentPb.isVisible = false
                sendCommentBt.isClickable = true
                commentTextInputEt.setText("")
                commentAdapter.comment += it
                Log.d("CommentFragment: ",it.toString())
            }
        })

         viewModel.getCommentsForPostStatus.observe(viewLifecycleOwner, EventObserver(
             onError = { error ->
                 snackbar(error)
                 Log.d("CommentFragment: ", error)
                 binding.apply {
                     commentPb.isVisible = false
                     sendCommentBt.isClickable = true
                 }
             },
             onLoading = {
                 binding.apply {
                     commentPb.isVisible = true
                     sendCommentBt.isClickable = false
                     commentAdapter.comment = listOf()
                 }
             }
         ){ commentList ->
             binding.apply {
                 commentPb.isVisible = false
                 sendCommentBt.isClickable = true
                 Log.d("CommentFragment: ", commentList.toString())
                 commentAdapter.comment = commentList
             }
         })

         viewModel.deleteCommentStatus.observe(viewLifecycleOwner, EventObserver(
             onError = {
                 snackbar(it)
                 binding.apply {
                     commentPb.isVisible = false
                 }
             },
             onLoading = {
                binding.commentPb.isVisible = true
             }
         ){
             commentAdapter.comment -= it
             binding.commentPb.isVisible = false
         })
     }
}