package com.project.findme.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
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
        val postLikeButton: ImageButton = binding.postLikeBtn
        val postCommentButton: ImageButton = binding.postCommentBtn
        val deletePostButton: ImageButton = binding.deleteBtn
        val homeProfileInfo = binding.homeProfileInfo
        val tvLikes = binding.tvLikes
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val binding = ItemPostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PostViewHolder(binding)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val post = posts[position]
        holder.apply {
            glide.load(post.imageUrl).into(postImageView)
            glide.load(post.authorProfilePictureUrl).into(profilePictureImageView)
            userNameTextView.text = post.authorUsername
            titleTextView.text = post.title
            descriptionTextView.text = post.text
            tvLikes.text = "${post.likedBy.count()} likes"
            val uid = FirebaseAuth.getInstance().uid!!
            deletePostButton.apply {
                isVisible = uid == post.authorUid
                setOnClickListener {
                    deleteButtonCLickedListener?.let { click ->
                        click(post)
                    }
                }
            }

            postLikeButton.setImageResource(
                if (post.isLiked) R.drawable.ic_liked
                else R.drawable.ic_heart
            )

            postCommentButton.setOnClickListener {
                commentButtonClickedListener?.let { click ->
                    click(post)
                }
            }

            postLikeButton.setOnClickListener {
                likeButtonClickListener?.let { click ->
                    click(post, position)
                }
            }

            var doubleClickLastTime = 0L
            postImageView.setOnClickListener {
                if (System.currentTimeMillis() - doubleClickLastTime < 300) {
                    doubleClickLastTime = 0
                    likeButtonClickListener?.let { click ->
                        click(post, position)
                    }
                } else {
                    doubleClickLastTime = System.currentTimeMillis()
                }
            }

            homeProfileInfo.setOnClickListener {
                navigateToProfile?.let { click ->
                    click(post.authorUid, post.authorUsername)
                }
            }
            tvLikes.setOnClickListener {
                likedByClickListener?.let { click ->
                    click(post.id)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return posts.size
    }

    private var commentButtonClickedListener: ((Post) -> Unit)? = null
    private var deleteButtonCLickedListener: ((Post) -> Unit)? = null
    private var likedByClickListener: ((String) -> Unit)? = null
    private var likeButtonClickListener: ((Post, Int) -> Unit)? = null
    private var navigateToProfile: ((String, String) -> Unit)? = null

    fun setOnCommentClickListener(listener: (Post) -> Unit) {
        commentButtonClickedListener = listener
    }

    fun setOnDeleteClickListener(listener: (Post) -> Unit) {
        deleteButtonCLickedListener = listener
    }

    fun setOnLikeClickListener(listener: (Post, Int) -> Unit) {
        likeButtonClickListener = listener
    }

    fun setNavigateToProfileListener(listener: (String, String) -> Unit) {
        navigateToProfile = listener
    }

    fun setOnLikedByClickListener(listener: (String) -> Unit) {
        likedByClickListener = listener
    }
}