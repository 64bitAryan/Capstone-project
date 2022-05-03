package com.project.findme.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.google.firebase.auth.FirebaseAuth
import com.project.findme.data.entity.Message
import com.ryan.findme.databinding.ItemFromBinding
import com.ryan.findme.databinding.ItemToBinding
import javax.inject.Inject

class MessageAdapter
    @Inject constructor(
    private val  glide: RequestManager
): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    inner class FromMessageViewHolder(fromBinding: ItemFromBinding):RecyclerView.ViewHolder(fromBinding.root){
        val ivProfilePicture:ImageView = fromBinding.profileIv
        private val tvMessage:TextView = fromBinding.messageFromTv

        fun bindView(message: Message){
            tvMessage.text = message.message
        }
    }

    inner class ToMessageViewHolder(toBinding: ItemToBinding): RecyclerView.ViewHolder(toBinding.root) {
        val ivProfilePicture:ImageView = toBinding.profileIv
        val tvMessage:TextView = toBinding.messageToTv

        fun bindView(message: Message){
            tvMessage.text = message.message
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(viewType == 1){
            val binding = ItemFromBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            FromMessageViewHolder(binding)
        } else {
            val binding = ItemToBinding.inflate(LayoutInflater.from(parent.context), parent, false)
            ToMessageViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == 1){
            (holder as FromMessageViewHolder).bindView(messages[position]!!)
        } else{
            (holder as ToMessageViewHolder).bindView(messages[position]!!)
        }
    }

    override fun getItemCount(): Int {
        return messages.size

    }

    override fun getItemViewType(position: Int): Int {
        return if(messages[position]?.fromId == FirebaseAuth.getInstance().currentUser?.uid) 1 else 0
    }

    private val diffCallBack = object: DiffUtil.ItemCallback<Message>() {
        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
            return oldItem.id == newItem.id
        }
    }
    private val differ = AsyncListDiffer(this, diffCallBack)

    var messages: List<Message?>
        get() = differ.currentList
        set(value) = differ.submitList(value)
}