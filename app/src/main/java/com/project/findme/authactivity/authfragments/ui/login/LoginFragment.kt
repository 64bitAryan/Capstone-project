package com.project.findme.authactivity.authfragments.ui.login

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentLoginUserBinding

class LoginFragment : Fragment(R.layout.fragment_login_user) {

    private val viewModel: LoginViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentLoginUserBinding.bind(view)

        binding.apply {

            editTextEmailLogin.setText(viewModel.email)
            editTextPasswordLogin.setText(viewModel.email)

            editTextEmailLogin.addTextChangedListener {
                viewModel.email = it.toString()
            }
            editTextPasswordLogin.addTextChangedListener {
                viewModel.password = it.toString()
            }
            textViewLoginToRegister.setOnClickListener {
                val action = LoginFragmentDirections.actionGlobalRegisterFragment()
                findNavController().navigate(action)
            }
            textViewForgotPassword.setOnClickListener {
                val action = LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment()
                findNavController().navigate(action)
            }
        }
    }
}