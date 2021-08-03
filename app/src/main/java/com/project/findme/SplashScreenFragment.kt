package com.project.findme

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentSplashScreenBinding

class SplashScreenFragment : Fragment(R.layout.fragment_splash_screen) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSplashScreenBinding.bind(view)
        binding.apply {
            buttonNavigateToRegisterScreen.setOnClickListener{
                val action = SplashScreenFragmentDirections.actionGlobalRegisterFragment()
                findNavController().navigate(action)
            }
            textViewAlreadyUserLogin.setOnClickListener{
                val action = SplashScreenFragmentDirections.actionGlobalLoginFragment()
                findNavController().navigate(action)
            }
        }
    }
}