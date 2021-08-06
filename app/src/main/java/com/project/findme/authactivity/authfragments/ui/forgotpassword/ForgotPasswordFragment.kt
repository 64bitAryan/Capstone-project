package com.project.findme.authactivity.authfragments.ui.forgotpassword

import android.os.Bundle
import android.view.View
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.project.findme.utils.exhaustive
import com.project.findme.utils.snackbar
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentForgotPasswordBinding
import kotlinx.coroutines.flow.collect

class ForgotPasswordFragment : Fragment(R.layout.fragment_forgot_password) {

    private val viewModel: ForgotPasswordViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentForgotPasswordBinding.bind(view)

        binding.apply {

            editTextEmailForgot.setText(viewModel.email)

            editTextEmailForgot.addTextChangedListener {
                viewModel.email = it.toString()
            }
            buttonForgotPassword.setOnClickListener {
                viewModel.onForgotPasswordConfirmClick(editTextEmailForgot.text.toString())
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.forgotPasswordEvent.collect { event ->
                when (event) {
                    is ForgotPasswordViewModel.ForgotPasswordEvent.ShowErrorMessage -> {
                        snackbar(event.msg)
                        val action = ForgotPasswordFragmentDirections.actionGlobalLoginFragment()
                        findNavController().navigate(action)
                    }
                    is ForgotPasswordViewModel.ForgotPasswordEvent.ShowPositiveMessage -> {
                        snackbar(event.msg)
                    }
                }.exhaustive
            }
        }

    }

}