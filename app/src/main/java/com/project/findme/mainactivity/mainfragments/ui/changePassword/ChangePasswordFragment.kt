package com.project.findme.mainactivity.mainfragments.ui.changePassword

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.project.findme.utils.EventObserver
import com.project.findme.utils.hideKeyboard
import com.project.findme.utils.snackbar
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentChangePasswordBinding
import com.ryan.findme.databinding.FragmentEditProfileBinding

class ChangePasswordFragment : Fragment(R.layout.fragment_change_password) {

    private lateinit var viewModel: ChangePasswordViewModel
    private lateinit var binding: FragmentChangePasswordBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(ChangePasswordViewModel::class.java)
        subscribeToObserve()

        binding = FragmentChangePasswordBinding.bind(view)

        binding.apply {

            etEditUserPassword.setText(viewModel.oldPassword)
            etEditUserNewPassword.setText(viewModel.newPassword)

            etEditUserPassword.addTextChangedListener {
                viewModel.oldPassword = it.toString()
            }

            etEditUserNewPassword.addTextChangedListener {
                viewModel.newPassword = it.toString()
            }

            btnChangePassword.setOnClickListener {
                hideKeyboard(activity as Activity)
                viewModel.changePassword()
            }
        }

    }

    private fun subscribeToObserve() {

        viewModel.changePasswordStatus.observe(viewLifecycleOwner, EventObserver(

            onError = {
                showProgress(false)
                snackbar(it)
            },
            onLoading = {
                showProgress(true)
            }
        ) {
            showProgress(false)
            snackbar("Password changed successfully!")
        })
    }

    private fun showProgress(bool: Boolean) {
        binding.apply {
            cvProgressChangePassword.isVisible = bool
            if (bool) {
                parentLayoutChangePassword.alpha = 0.5f
                activity?.window!!.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            } else {
                parentLayoutChangePassword.alpha = 1f
                activity?.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
    }

}