package com.project.findme.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.project.findme.data.entity.User
import com.ryan.findme.databinding.SearchListLayoutBinding
import javax.inject.Inject

class UserAdapter
    @Inject constructor(
        private val glide: RequestManager
    ): RecyclerView.Adapter<UserAdapter.UserViewHolder>(){

    inner class UserViewHolder(binding: SearchListLayoutBinding):RecyclerView.ViewHolder(binding.root){
        val ivProfilePicture: ImageView = binding.imageViewProfilePictureSearchList
        val tvUsername: TextView = binding.textViewUserNameSearchList
        val tvDescription: TextView = binding.textViewDescriptionSearchList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = SearchListLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = users[position]
        holder.apply {
            glide.load(user.profilePicture).into(ivProfilePicture)
            tvUsername.text = user.userName
            tvDescription.text = user.description

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

    private val diffCallBack = object: DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.uid == newItem.uid
        }
    }
    private val differ = AsyncListDiffer(this, diffCallBack)

    var users: List<User>
        get() = differ.currentList
        set(value) = differ.submitList(value)

    private var onUserClickListener: ((User) -> Unit)? = null

    fun setOnUserClickListener(listener: (User) -> Unit) {
        onUserClickListener = listener
    }
}