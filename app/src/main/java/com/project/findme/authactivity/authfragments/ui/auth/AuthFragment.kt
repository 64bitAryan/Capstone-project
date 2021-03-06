package com.project.findme.authactivity.authfragments.ui.auth

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.project.findme.credentialactivity.CredentialActivity
import com.project.findme.data.entity.User
import com.project.findme.mainactivity.MainActivity
import com.project.findme.utils.EventObserver
import com.project.findme.utils.snackbar
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentAuthScreenBinding
import dagger.hilt.android.AndroidEntryPoint

const val REQUEST_CODE_SIGN_IN = 0

@AndroidEntryPoint
class AuthFragment : Fragment(R.layout.fragment_auth_screen) {

    private val viewModel: AuthViewModel by viewModels()
    private lateinit var binding: FragmentAuthScreenBinding
    private val users = FirebaseFirestore.getInstance().collection("users")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        subscribeToObserver()
        binding = FragmentAuthScreenBinding.bind(view)
        binding.apply {
            buttonNavigateToRegisterScreen.setOnClickListener {
                findNavController().navigate(AuthFragmentDirections.actionGlobalRegisterFragment())
            }
            textViewAlreadyUserLogin.setOnClickListener {
                findNavController().navigate(AuthFragmentDirections.actionGlobalLoginFragment())
            }
            buttonGoogleSignIn.setOnClickListener {
                viewModel.googleRegisterStatus
                val option = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .requestProfile()
                    .build()
                val signInClient = GoogleSignIn.getClient(requireActivity(), option)
                signInClient.signInIntent.also {
                    resultLauncher.launch(it)
                }
            }
        }
    }

    var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val account = GoogleSignIn.getSignedInAccountFromIntent(data)
                account.let {
                    viewModel.onSignInGoogleButtonClick(it)
                }
            }
        }

    private fun subscribeToObserver() {
        binding = FragmentAuthScreenBinding.inflate(layoutInflater)
        viewModel.googleRegisterStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.apply {
                    authProgressbar.isVisible = false
                    buttonGoogleSignIn.isEnabled = true
                    buttonNavigateToRegisterScreen.isEnabled = true
                    textViewAlreadyUserLogin.isClickable = true
                }
                snackbar(it)
            },
            onLoading = {
                binding.apply {
                    authProgressbar.isVisible = true
                    buttonGoogleSignIn.isEnabled = false
                    buttonNavigateToRegisterScreen.isEnabled = false
                    textViewAlreadyUserLogin.isClickable = false
                }
            }
        ) { authResult ->
            binding.apply {
                buttonGoogleSignIn.isEnabled = false
                buttonNavigateToRegisterScreen.isEnabled = false
                textViewAlreadyUserLogin.isClickable = false
            }

            FirebaseAuth.getInstance().currentUser?.uid?.let { id ->
                FirebaseFirestore.getInstance().collection("credentials").whereEqualTo("uid", id)
                    .get()
                    .addOnSuccessListener { document ->
                        binding.authProgressbar.isVisible = false
                        if (!document.isEmpty) {
                            Intent(requireContext(), MainActivity::class.java).also { intent ->
                                startActivity(intent)
                                requireActivity().finish()
                            }
                        } else {
                            val uid = authResult.user?.uid!!
                            val username = authResult.user?.displayName.toString()
                            val user = User(uid, username)
                            users.document(uid).set(user)
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
}