package com.project.findme.mainactivity.mainfragments.ui.signout

import android.app.AlertDialog
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.project.findme.authactivity.AuthActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@AndroidEntryPoint
class SignOutDialogFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog =
        AlertDialog.Builder(requireContext())
            .setTitle("Sign out?")
            .setMessage("Do you want to sign out from app?")
            .setPositiveButton("Yes"){_, _ ->
                CoroutineScope(Dispatchers.IO).launch {
                    Firebase.auth.signOut()
                    withContext(Dispatchers.Main){
                        Intent(requireContext(), AuthActivity::class.java).also {
                            startActivity(it)
                            activity?.finish()
                        }
                    }
                }
            }.create()

}