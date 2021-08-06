package com.project.findme.authactivity.authfragments.ui.register

import android.os.Bundle
import android.view.View
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
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

        binding = FragmentRegisterUserBinding.bind(view)
        binding.apply {

            editTextUsernameRegister.setText(viewModel.uname)
            editTextEmailRegister.setText(viewModel.email)
            editTextPasswordRegister.setText(viewModel.password)
            editTextConfirmPasswordRegister.setText(viewModel.cpassword)

            editTextUsernameRegister.addTextChangedListener {
                viewModel.uname = it.toString()
            }
            editTextEmailRegister.addTextChangedListener {
                viewModel.email = it.toString()
            }
            editTextPasswordRegister.addTextChangedListener {
                viewModel.password = it.toString()
            }
            editTextConfirmPasswordRegister.addTextChangedListener {
                viewModel.cpassword = it.toString()
            }

            textViewRegisterToLogin.setOnClickListener {
                val action = RegisterFragmentDirections.actionGlobalLoginFragment()
                findNavController().navigate(action)
            }

            subscribeToObserve()

            buttonRegisterUser.setOnClickListener {
                viewModel.register(
                    email = editTextEmailRegister.text.toString(),
                    password = editTextPasswordRegister.text.toString(),
                    repeatedPassword = editTextConfirmPasswordRegister.text.toString(),
                    username = editTextUsernameRegister.text.toString()
                )
            }
            textViewRegisterToLogin.setOnClickListener {
                if(findNavController().previousBackStackEntry != null){
                    findNavController().popBackStack()
                } else {
                    findNavController().navigate(
                        RegisterFragmentDirections.actionGlobalLoginFragment()
                    )
                }
            }
        }
    }

    private fun subscribeToObserve() {
        binding = FragmentRegisterUserBinding.inflate(layoutInflater)
        viewModel.registerStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.registerProgressbar.isVisible = false
                snackbar(it)
            },
            onLoading = {
                binding.registerProgressbar.isVisible = true
            }
        ){
            binding.registerProgressbar.isVisible = false
            snackbar(getString(R.string.success_registration))
        })
    }
}