package com.project.findme.authactivity.authfragments.ui.register

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentRegisterUserBinding

class RegisterFragment : Fragment(R.layout.fragment_register_user) {

    private val viewModel : RegisterViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentRegisterUserBinding.bind(view)
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
        }
    }
}