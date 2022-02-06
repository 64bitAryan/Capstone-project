package com.project.findme.authactivity.authfragments.ui.forgotpassword

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.project.findme.utils.EventObserver
import com.project.findme.utils.Events
import com.project.findme.utils.hideKeyboard
import com.project.findme.utils.snackbar
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentForgotPasswordBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ForgotPasswordFragment : Fragment(R.layout.fragment_forgot_password) {

    private lateinit var viewModel: ForgotPasswordViewModel
    private lateinit var binding: FragmentForgotPasswordBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(ForgotPasswordViewModel::class.java)
        subscribeToObserve()


        binding = FragmentForgotPasswordBinding.bind(view)

        binding.apply {

            editTextEmailForgot.setText(viewModel.email)

            editTextEmailForgot.addTextChangedListener {
                viewModel.email = it.toString()
            }

            buttonForgotPassword.setOnClickListener {
                hideKeyboard(activity as Activity)
                viewModel.onForgotPasswordConfirmClick()
            }

        }
    }

    private fun subscribeToObserve() {
        binding = FragmentForgotPasswordBinding.inflate(layoutInflater)
        viewModel.forgotPasswordStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                showProgress(false)
                snackbar(it)
            },
            onLoading = {
                showProgress(true)
            }
        ) {
            showProgress(false)
            when (it) {
                true -> {
                    snackbar("Link Send to your Registered Email")
                    findNavController().navigate(ForgotPasswordFragmentDirections.actionGlobalLoginFragment())

                }
                false -> snackbar("Enter a valid Email or Register with new")
            }
        })
    }

    private fun showProgress(bool: Boolean) {
        binding.apply {
            cvProgressForgot.isVisible = bool
            if (bool) {
                parentLayoutForgot.alpha = 0.5f
                activity?.window!!.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            } else {
                parentLayoutForgot.alpha = 1f
                activity?.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        binding = FragmentForgotPasswordBinding.inflate(layoutInflater)
        binding.editTextEmailForgot.setText("")
    }

}