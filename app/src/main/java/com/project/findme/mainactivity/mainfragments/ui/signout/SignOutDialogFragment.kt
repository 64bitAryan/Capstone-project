package com.project.findme.mainactivity.mainfragments.ui.signout

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.project.findme.authactivity.AuthActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SignOutDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Sign out?")
            .setMessage("Do you want to sign out from app?")
            .setPositiveButton("Yes", null)
            .create()

        dialog.setOnShowListener {
            val button = dialog.getButton(androidx.appcompat.app.AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener {
                CoroutineScope(Dispatchers.Main).launch {
                    val option =
                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_GAMES_SIGN_IN)
                            .build()
                    val signInClient = GoogleSignIn.getClient(requireActivity(), option)
                    signInClient.signOut().addOnSuccessListener {

                        Intent(requireContext(), AuthActivity::class.java).also { intent ->
                            startActivity(intent)
                            requireActivity().finish()

                        }
                    }
                    Firebase.auth.signOut()

                    Intent(requireContext(), AuthActivity::class.java).also { intent ->
                        startActivity(intent)
                        requireActivity().finish()

                    }
                }
            }
        }
        return dialog

    }

}