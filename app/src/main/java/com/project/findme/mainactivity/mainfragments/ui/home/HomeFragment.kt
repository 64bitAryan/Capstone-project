package com.project.findme.mainactivity.mainfragments.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentHomeScreenBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home_screen) {
    private lateinit var binding: FragmentHomeScreenBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeScreenBinding.bind(view)

        binding.apply {
            createPostFb.setOnClickListener {
                findNavController().navigate(R.id.action_homeFragment_to_createPostFragment)
            }
        }
    }
}