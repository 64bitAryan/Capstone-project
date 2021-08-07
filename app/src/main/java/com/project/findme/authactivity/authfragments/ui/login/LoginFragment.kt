package com.project.findme.authactivity.authfragments.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.findme.mainactivity.MainActivity
import com.project.findme.utils.EventObserver
import com.project.findme.utils.hideKeyboard
import com.project.findme.utils.snackbar
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentLoginUserBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment(R.layout.fragment_login_user) {

    private lateinit var viewModel: LoginViewModel
    private lateinit var binding: FragmentLoginUserBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(LoginViewModel::class.java)
        subscribeToObserve()

        binding = FragmentLoginUserBinding.bind(view)

        binding.apply {

            editTextEmailLogin.setText(viewModel.email)
            editTextPasswordLogin.setText(viewModel.password)

            editTextEmailLogin.addTextChangedListener{
                viewModel.email = it.toString()
            }

            editTextPasswordLogin.addTextChangedListener {
                viewModel.password = it.toString()
            }

            textViewLoginToRegister.setOnClickListener {
                findNavController().navigate(
                    LoginFragmentDirections.actionGlobalRegisterFragment()
                )
            }

            textViewForgotPassword.setOnClickListener {
                findNavController().navigate(
                    LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment()
                )
            }

            buttonLoginUser.setOnClickListener {
                hideKeyboard(activity as Activity)
                viewModel.login()
            }

        }
    }

    private fun subscribeToObserve() {
        binding = FragmentLoginUserBinding.inflate(layoutInflater)
        viewModel.loginStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.apply {
                    loginProgressbar.isVisible = false
                    buttonLoginUser.isEnabled = true
                    textViewForgotPassword.isClickable = true
                    textViewLoginToRegister.isClickable = true
                }
                snackbar(it)
            },
            onLoading = {
                binding.apply {
                    loginProgressbar.isVisible = true
                    buttonLoginUser.isEnabled = false
                    textViewForgotPassword.isClickable = false
                    binding.textViewLoginToRegister.isClickable = false
                }
            }
        ) {
            binding.loginProgressbar.isVisible = false
            Intent(requireContext(), MainActivity::class.java).also {
                startActivity(it)
                requireActivity().finish()
            }
        })
    }
}