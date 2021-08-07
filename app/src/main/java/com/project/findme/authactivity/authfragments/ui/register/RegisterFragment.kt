package com.project.findme.authactivity.authfragments.ui.register

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.findme.utils.EventObserver
import com.project.findme.utils.snackbar
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentRegisterUserBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RegisterFragment : Fragment(R.layout.fragment_register_user) {

    private lateinit var viewModel: RegisterViewModel

    private lateinit var binding: FragmentRegisterUserBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(RegisterViewModel::class.java)
        subscribeToObserve()

        binding = FragmentRegisterUserBinding.bind(view)
        binding.apply {

            editTextUsernameRegister.setText(viewModel.username)
            editTextEmailRegister.setText(viewModel.email)
            editTextPasswordRegister.setText(viewModel.password)
            editTextConfirmPasswordRegister.setText(viewModel.repeatedPassword)

            editTextUsernameRegister.addTextChangedListener {
                viewModel.username = it.toString()
            }
            editTextEmailRegister.addTextChangedListener {
                viewModel.email = it.toString()
            }
            editTextPasswordRegister.addTextChangedListener {
                viewModel.password = it.toString()
            }
            editTextConfirmPasswordRegister.addTextChangedListener {
                viewModel.repeatedPassword = it.toString()
            }

            textViewRegisterToLogin.setOnClickListener {
                val action = RegisterFragmentDirections.actionGlobalLoginFragment()
                findNavController().navigate(action)
            }

            buttonRegisterUser.setOnClickListener {
                viewModel.register()
            }
            textViewRegisterToLogin.setOnClickListener {
                findNavController().navigate(
                    RegisterFragmentDirections.actionGlobalLoginFragment()
                )
            }
        }
    }

    private fun subscribeToObserve() {
        binding = FragmentRegisterUserBinding.inflate(layoutInflater)
        viewModel.registerStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.registerProgressbar.isVisible = false
                binding.buttonRegisterUser.isEnabled = true
                binding.textViewRegisterToLogin.linksClickable = true

                snackbar(it)
            },
            onLoading = {
                binding.buttonRegisterUser.isEnabled = false
                binding.textViewRegisterToLogin.linksClickable = false
                binding.registerProgressbar.isVisible = true
            }
        ) {
            binding.registerProgressbar.isVisible = false
            snackbar(getString(R.string.success_registration))
            findNavController().navigate(RegisterFragmentDirections.actionGlobalLoginFragment())
        })
    }
}