package com.project.findme.mainactivity.mainfragments.ui.comment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.auth.FirebaseAuth
import com.project.findme.adapter.CommentAdapter
import com.project.findme.data.entity.Comment
import com.project.findme.utils.EventObserver
import com.project.findme.utils.hideKeyboard
import com.project.findme.utils.showKeyboard
import com.project.findme.utils.snackbar
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentCommentBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class CommentFragment : Fragment(R.layout.fragment_comment) {

    @Inject
    lateinit var commentAdapter: CommentAdapter
    private lateinit var binding: FragmentCommentBinding
    private val viewModel: CommentViewModel by viewModels()
    private val args: CommentFragmentArgs by navArgs()
    private var parent: String? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCommentBinding.bind(view)
        binding.apply {
            sendCommentBt.setOnClickListener {
                hideKeyboard(requireActivity())
                val text = commentTextInputEt.text.toString()
                viewModel.createComment(text, args.postId, parent)
                commentTextInputEt.setText("")
            }
        }
        viewModel.getCommentForPost(args.postId)
        setupRecyclerView()
        subscribeToObserve()
    }

    private fun showConfirmationDialog(comment: Comment) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Comment")
            .setMessage("Are you sure you want to delete this comment?")
            .setPositiveButton(
                "Yes"
            ) { _, _ ->
                viewModel.deleteComment(comment)
            }
            .setNegativeButton("No", null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    private fun setupRecyclerView() {
        binding.commentRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = commentAdapter
        }
        commentAdapter.setDeleteListener { comment ->
            hideKeyboard(requireActivity())
            showConfirmationDialog(comment)
        }
        commentAdapter.setNavigateToProfileListener { uid, userName ->
            if (FirebaseAuth.getInstance().currentUser?.uid == uid) {
                findNavController().navigate(
                    CommentFragmentDirections.actionCommentFragmentToUserProfileFragment()
                )
            } else {
                findNavController().navigate(
                    CommentFragmentDirections.actionCommentFragmentToSearchedProfileFragment(
                        uid = uid,
                        username = userName
                    )
                )
            }
        }
        commentAdapter.setReplyToCommentListener { uid ->
            showKeyboard(requireActivity(), binding.commentTextInputEt)
            parent = uid
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
        ) {
            setupRecyclerView()
            binding.commentPb.isVisible = false
            viewModel.getCommentForPost(it.postId)
        })

        viewModel.getCommentsForPostStatus.observe(viewLifecycleOwner, EventObserver(
            onError = { error ->
                snackbar(error)
                Log.d("CommentsFragment", error)
                binding.apply {
                    commentPb.isVisible = false
                    sendCommentBt.isClickable = true
                }
            },
            onLoading = {
                binding.apply {
                    commentPb.isVisible = true
                    sendCommentBt.isClickable = false
                    commentAdapter.comments = listOf()
                }
            }
        ) { commentList ->
            binding.apply {
                commentPb.isVisible = false
                sendCommentBt.isClickable = true
                commentAdapter.comments = commentList
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
        ) {
            setupRecyclerView()
            viewModel.getCommentForPost(it.postId)
            binding.commentPb.isVisible = false
        })
    }
}