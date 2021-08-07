package com.project.findme.authactivity.authfragments.ui.auth

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentAuthScreenBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthFragment : Fragment(R.layout.fragment_auth_screen) {

    private val viewModel : AuthViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
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
            buttonGoogleSignIn.setOnClickListener {
                viewModel.onSignInGoogleButtonClick()
            }
        }

    }
}