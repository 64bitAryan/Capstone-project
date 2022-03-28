package com.project.findme.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.project.findme.data.entity.Comment
import com.ryan.findme.databinding.ItemCommentBinding
import javax.inject.Inject

class CommentAdapter @Inject constructor(
    private val glide : RequestManager
): RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Comment>() {

        override fun areContentsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areItemsTheSame(oldItem: Comment, newItem: Comment): Boolean {
            return oldItem.uid == newItem.uid
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var comment: List<Comment>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    inner class CommentViewHolder(binding: ItemCommentBinding): RecyclerView.ViewHolder(binding.root) {
        val nameTextView = binding.nameTv
        val profileImageView = binding.profileIv
        val commentTextTextView = binding.commentTextTv
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comment[position]
        holder.apply {
            nameTextView.text = comment.uesrname
            glide.load(comment.profilePicture).into(profileImageView)
            commentTextTextView.text = comment.comment
        }
    }

    override fun getItemCount(): Int {
        return comment.size
    }
}