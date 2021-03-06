package com.project.findme.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.core.view.isVisible
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.findme.data.entity.Post
import com.project.findme.data.entity.User
import com.ryan.findme.databinding.ItemFollowersBinding
import javax.inject.Inject

class ListAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<ListAdapter.ListViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<User>() {
        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.uid == newItem.uid
        }
    }

    private val differ = AsyncListDiffer(this, diffCallback)

    var users: List<User>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    inner class ListViewHolder(binding: ItemFollowersBinding) :
        RecyclerView.ViewHolder(binding.root) {
        val usernameTextView: TextView = binding.textViewUserNameList
        val nameTextView: TextView = binding.textViewNameList
        val profilePictureImageView: ImageView = binding.imageViewProfilePictureList
        val btnFollow: AppCompatButton = binding.btnListFollowUser
        val btnUnfollow: AppCompatButton = binding.btnListFollowingUser
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListAdapter.ListViewHolder {
        val binding = ItemFollowersBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ListAdapter.ListViewHolder, position: Int) {
        val user = users[position]
        holder.apply {
            glide.load(user.profilePicture).into(profilePictureImageView)
            usernameTextView.text = user.userName
            nameTextView.text = user.credential.name
            val uid = FirebaseAuth.getInstance().uid!!
            if (uid in user.follows) {
                btnFollow.visibility = View.INVISIBLE
                btnUnfollow.visibility = View.VISIBLE
            } else {
                btnUnfollow.visibility = View.INVISIBLE
                btnFollow.visibility = View.VISIBLE
            }
            if (uid == user.uid){
                btnFollow.visibility = View.INVISIBLE
                btnUnfollow.visibility = View.INVISIBLE
            }

            btnFollow.setOnClickListener {
                onFollowClickListener?.let {
                    it(user.uid)
                }
            }

            btnUnfollow.setOnClickListener {
                onUnFollowClickListener?.let {
                    it(user.uid)
                }
            }

            itemView.setOnClickListener {
                onUserClickListener?.let {  click ->
                    click(user)
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return users.size
    }

    private var onUserClickListener: ((User) -> Unit)? = null

    fun setOnUserClickListener(listener: (User) -> Unit) {
        onUserClickListener = listener
    }

    private var onFollowClickListener: ((String) -> Unit)? = null

    fun setOnFollowClickListener(listener: (String) -> Unit) {
        onFollowClickListener = listener
    }

    private var onUnFollowClickListener: ((String) -> Unit)? = null

    fun setOnUnFollowClickListener(listener: (String) -> Unit) {
        onUnFollowClickListener = listener
    }
}