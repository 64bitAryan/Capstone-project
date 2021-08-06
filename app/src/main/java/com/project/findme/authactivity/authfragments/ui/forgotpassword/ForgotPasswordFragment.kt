package com.project.findme.authactivity.authfragments.ui.forgotpassword

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
import com.ryan.findme.databinding.FragmentForgotPasswordBinding

class ForgotPasswordFragment : Fragment(R.layout.fragment_forgot_password) {

    private lateinit var viewModel: ForgotPasswordViewModel
    private lateinit var binding : FragmentForgotPasswordBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(ForgotPasswordViewModel::class.java)


        binding = FragmentForgotPasswordBinding.bind(view)

        binding.apply {

            editTextEmailForgot.setText(viewModel.email)

            editTextEmailForgot.addTextChangedListener {
                viewModel.email = it.toString()
            }

            subscribeToObserve()

            buttonForgotPassword.setOnClickListener {
                viewModel.onForgotPasswordConfirmClick()
            }

        }
    }

    private fun subscribeToObserve() {
        binding = FragmentForgotPasswordBinding.inflate(layoutInflater)
        viewModel.forgotPasswordStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.forgotPasswordProgressbar.isVisible = false
                snackbar(it)
            },
            onLoading = {
                binding.forgotPasswordProgressbar.isVisible = true
            }
        ){
            binding.forgotPasswordProgressbar.isVisible = false
            snackbar("Reset link send to your email")
            findNavController().navigate(ForgotPasswordFragmentDirections.actionGlobalLoginFragment())
        })
    }

}