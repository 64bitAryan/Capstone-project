package com.project.findme.authactivity.authfragments.ui.login

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.findme.credentialactivity.CredentialActivity
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

            editTextEmailLogin.addTextChangedListener {
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
                    showProgress(false)
                }
                snackbar(it)
            },
            onLoading = {
                binding.apply {
                    showProgress(true)
                }
            }
        ) {
            showProgress(false)
            FirebaseAuth.getInstance().currentUser?.uid?.let {
                FirebaseFirestore.getInstance().collection("credentials").whereEqualTo("uid", it)
                    .get()
                    .addOnSuccessListener { document ->
                        if (!document.isEmpty) {
                            Intent(requireContext(), MainActivity::class.java).also { intent ->
                                startActivity(intent)
                                requireActivity().finish()
                            }
                        } else {
                            Intent(
                                requireContext(),
                                CredentialActivity::class.java
                            ).also { intent ->
                                startActivity(intent)
                                requireActivity().finish()
                            }
                        }
                    }
            }
        })
    }

    private fun showProgress(bool: Boolean) {
        binding.apply {
            cvProgressLogin.isVisible = bool
            if (bool) {
                parentLayoutLogin.alpha = 0.5f
                activity?.window!!.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            } else {
                parentLayoutLogin.alpha = 1f
                activity?.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                editTextPasswordLogin.setText("")
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        binding = FragmentLoginUserBinding.inflate(layoutInflater)
        binding.editTextEmailLogin.setText("")
        binding.editTextPasswordLogin.setText("")
    }

}