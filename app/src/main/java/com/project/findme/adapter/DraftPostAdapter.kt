package com.project.findme.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.project.findme.data.entity.Post
import com.project.findme.utils.Constants
import com.ryan.findme.databinding.ItemDraftPostsBinding
import javax.inject.Inject

class DraftPostAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<DraftPostAdapter.DraftPostViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Post>() {
        override fun areContentsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areItemsTheSame(oldItem: Post, newItem: Post): Boolean {
            return oldItem.id == newItem.id
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var posts: List<Post>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    inner class DraftPostViewHolder(binding: ItemDraftPostsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val postTitle: TextView = binding.tvDraftPostTitle
        val postDescription: TextView = binding.tvDraftPostDescription
        val postImage: ImageView = binding.ivDraftPostImage
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): DraftPostAdapter.DraftPostViewHolder {
        val binding = ItemDraftPostsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return DraftPostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: DraftPostAdapter.DraftPostViewHolder, position: Int) {
        val post = posts[position]
        holder.apply {
            if (post.imageUrl.isEmpty()) {
                glide.load(Constants.DEFAULT_PROFILE_PICTURE_URL).into(postImage)
            } else {
                glide.load(post.imageUrl).into(postImage)
            }
            postTitle.text = post.title
            postDescription.text = post.text
            itemView.setOnClickListener {
                onPostClickListener?.let { click ->
                    click(post)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    private var onPostClickListener: ((Post) -> Unit)? = null

    fun setOnPostClickListener(listener: (Post) -> Unit) {
        onPostClickListener = listener
    }
}