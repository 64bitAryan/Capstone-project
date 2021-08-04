package com.project.findme.auth.ui

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentAuthScreenBinding

class AuthFragment : Fragment(R.layout.fragment_auth_screen) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        (requireActivity() as AppCompatActivity).supportActionBar?.hide()
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentAuthScreenBinding.bind(view)
        binding.apply {
            buttonNavigateToRegisterScreen.setOnClickListener{
                val action = AuthFragmentDirections.actionGlobalRegisterFragment()
                findNavController().navigate(action)
            }
            textViewAlreadyUserLogin.setOnClickListener{
                val action = AuthFragmentDirections.actionGlobalLoginFragment()
                findNavController().navigate(action)
            }
        }

    }
}