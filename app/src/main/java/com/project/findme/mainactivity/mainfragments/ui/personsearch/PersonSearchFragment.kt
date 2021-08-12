package com.project.findme.mainactivity.mainfragments.ui.personsearch

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.project.findme.authactivity.authfragments.ui.forgotpassword.ForgotPasswordFragmentDirections
import com.project.findme.utils.EventObserver
import com.project.findme.utils.snackbar
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentForgotPasswordBinding
import com.ryan.findme.databinding.FragmentSearchPersonBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PersonSearchFragment : Fragment(R.layout.fragment_search_person){

    private lateinit var viewModel: PersonViewModel
    private lateinit var binding : FragmentSearchPersonBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(PersonViewModel::class.java)

        binding = FragmentSearchPersonBinding.bind(view)

        binding.apply {

            editTextSearchField.setText(viewModel.searchQuery.toString())

            editTextSearchField.addTextChangedListener {
                viewModel.searchQuery.value = it.toString()
                viewModel.searchPerson()
            }

            buttonSearchPerson.setOnClickListener {
                viewModel.searchPerson()
            }

            //recyclerViewSearchList.setAdapter()
        }
    }

    class UsersViewModel(itemView: View) : RecyclerView.ViewHolder(itemView){

        fun setDetails(context : Context, userName : String, description : String, profilePicture : String){

            val username = itemView.findViewById<TextView>(R.id.text_view_user_name_search_list)
            val userDescription = itemView.findViewById<TextView>(R.id.text_view_description_search_list)
            val profilepicture = itemView.findViewById<ImageView>(R.id.image_view_profile_picture_search_list)

            username.text = userName
            userDescription.text = description

            Glide.with(context).load(profilePicture).into(profilepicture)

        }
    }

    private fun subscribeToObserve(){
        binding = FragmentSearchPersonBinding.inflate(layoutInflater)
        viewModel.searchPersonStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                snackbar(it)
            },
            onLoading = {
            }
        ){

        })
    }

}