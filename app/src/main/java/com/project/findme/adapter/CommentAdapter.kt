package com.project.findme.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.view.isVisible
import androidx.core.view.setMargins
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.findme.data.entity.Comment
import com.project.findme.data.entity.User
import com.ryan.findme.databinding.ItemCommentBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class CommentAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    val users = FirebaseFirestore.getInstance().collection("users")
    private val c = FirebaseFirestore.getInstance().collection("comments")

    private val diffCallback = object : DiffUtil.ItemCallback<Comment>() {

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.uid == newItem.uid
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var comments: List<Comment>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    inner class CommentViewHolder(private val binding: ItemCommentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        private fun initBinding(binding: ItemCommentBinding, comment: Comment) {
            binding.apply {
                nameTv.text = comment.username
                glide.load(comment.profilePicture).into(profileIv)
                commentTextTv.text = comment.comment
                tvShowReplies.text = "Show ${comment.repliedComments.size} replies"
                tvShowReplies.isVisible = comment.repliedComments.isNotEmpty()
                commentDeleteIb.isVisible = comment.uid == FirebaseAuth.getInstance().uid!!
                commentDeleteIb.setOnClickListener {
                    deleteCommentListener?.let { click ->
                        click(comment)
                    }
                }

                commentProfileInfo.setOnClickListener {
                    navigateToProfile?.let { click ->
                        click(comment.uid, comment.username)
                    }
                }
                tvReplyToComment.setOnClickListener {
                    replyToCommentListener?.let { click ->
                        click(comment.commentId)
                    }
                }

                tvShowReplies.setOnClickListener {
                    layoutRepliedComments.isVisible = !layoutRepliedComments.isVisible
                    if (!layoutRepliedComments.isVisible) {
                        tvShowReplies.text = "Show ${comment.repliedComments.size} replies"
                        layoutRepliedComments.removeAllViews()
                    } else {
                        tvShowReplies.text = "Hide comments"
                        createNestedComment(binding, comment)
                    }
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun createNestedComment(binding: ItemCommentBinding, comment: Comment) {
            comment.repliedComments.forEach {
                CoroutineScope(Dispatchers.Main).launch {
                    val curComment = c.document(it).get().await().toObject(Comment::class.java)!!
                    val user =
                        users.document(curComment.uid).get().await().toObject(User::class.java)!!
                    curComment.username = user.userName
                    curComment.profilePicture = user.profilePicture
                    val newComment = ItemCommentBinding.inflate(
                        LayoutInflater.from(binding.root.context),
                        null,
                        false
                    )
                    initBinding(newComment, curComment)
                    binding.layoutRepliedComments.addView(newComment.root)
                }
            }
        }

        fun bind(comment: Comment) {
            initBinding(binding, comment)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CommentViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(comments[position])
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    private var deleteCommentListener: ((Comment) -> Unit)? = null
    private var navigateToProfile: ((String, String) -> Unit)? = null
    private var replyToCommentListener: ((String) -> Unit)? = null

    fun setDeleteListener(listener: (Comment) -> Unit) {
        deleteCommentListener = listener
    }

    fun setNavigateToProfileListener(listener: (String, String) -> Unit) {
        navigateToProfile = listener
    }

    fun setReplyToCommentListener(listener: (String) -> Unit) {
        replyToCommentListener = listener
    }
}