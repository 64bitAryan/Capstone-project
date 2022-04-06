package com.project.findme.adapter

import android.R.attr.button
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.google.firebase.auth.FirebaseAuth
import com.project.findme.data.entity.Post
import com.ryan.findme.R
import com.ryan.findme.databinding.ItemPostBinding
import javax.inject.Inject


class PostAdapter @Inject constructor(
    private val glide: RequestManager,
) : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {

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

    inner class PostViewHolder(binding: ItemPostBinding) : RecyclerView.ViewHolder(binding.root) {
        val descriptionTextView: TextView = binding.descriptionTv
        val titleTextView = binding.postTitle
        val userNameTextView: TextView = binding.postUsernameTv
        val postImageView: ImageView = binding.postIv
        val profilePictureImageView: ImageView = binding.postProfilePic
        val postLikeButton: Button = binding.postLikeBtn
        val postCommentButton: ImageButton = binding.postCommentBtn
        val deletePostButton: ImageButton = binding.deleteBtn
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(
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
            glide.load(post.authorProfilePictureUrl).into(profilePictureImageView)
            userNameTextView.text = post.authorUsername
            titleTextView.text = post.title
            descriptionTextView.text = post.text
            postLikeButton.text = post.likedBy.count().toString()
            val uid = FirebaseAuth.getInstance().uid!!
            deletePostButton.apply {
                isVisible = uid == post.authorUid
                setOnClickListener {
                    deleteButtonCLickedListener?.let { click ->
                        click(post)
                    }
                }
            }


            val imgLiked: Drawable = ResourcesCompat.getDrawable(
                postLikeButton.context.resources,
                R.drawable.ic_liked,
                null
            )!!

            val imgNotLiked: Drawable = ResourcesCompat.getDrawable(
                postLikeButton.context.resources,
                R.drawable.ic_heart,
                null
            )!!

            postLikeButton.setCompoundDrawablesWithIntrinsicBounds(
                if (uid in post.likedBy) imgLiked
                else imgNotLiked,
                null,
                null,
                null
            )

            postCommentButton.setOnClickListener {
                commentButtonClickedListener?.let { click ->
                    click(post)
                }
            }

            postLikeButton.setOnClickListener {
                likeButtonClickListener?.let { click ->
                    click(post)
                }
            }

            var doubleClickLastTime = 0L
            postImageView.setOnClickListener {
                if (System.currentTimeMillis() - doubleClickLastTime < 300) {
                    doubleClickLastTime = 0
                    likeButtonClickListener?.let { click ->
                        click(post)
                    }
                } else {
                    doubleClickLastTime = System.currentTimeMillis()
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    private var commentButtonClickedListener: ((Post) -> Unit)? = null
    private var deleteButtonCLickedListener: ((Post) -> Unit)? = null
    private var likeButtonClickListener: ((Post) -> Unit)? = null

    fun setOnCommentClickListener(listener: (Post) -> Unit) {
        commentButtonClickedListener = listener
    }

    fun setOnDeleteClickListener(listener: (Post) -> Unit) {
        deleteButtonCLickedListener = listener
    }

    fun setOnLikeClickListener(listener: (Post) -> Unit) {
        likeButtonClickListener = listener
    }
}