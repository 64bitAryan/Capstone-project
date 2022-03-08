package com.project.findme.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.project.findme.data.entity.Post
import com.ryan.findme.databinding.ItemPostProfileBinding
import javax.inject.Inject

class PostAdapterProfile @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<PostAdapterProfile.PostViewHolder>() {

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

    inner class PostViewHolder(binding: ItemPostProfileBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val postImageView: ImageView = binding.postIv
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostProfileBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.apply {
            glide.load(post.imageUrl).into(postImageView)

            itemView.setOnClickListener {
                onItemClickListener?.let {
                    it(post)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    private var onItemClickListener: ((Post) -> Unit)? = null

    fun setOnItemClickListener(listener: (Post) -> Unit) {
        onItemClickListener = listener
    }
}