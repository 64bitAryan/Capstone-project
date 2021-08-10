package com.project.findme.mainactivity.mainfragments.ui.signout

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.project.findme.authactivity.AuthActivity
import com.project.findme.utils.EventObserver
import com.project.findme.utils.snackbar
import com.ryan.findme.R
import com.ryan.findme.databinding.DialogFragmentSignoutBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SignOutDialogFragment : DialogFragment() {

    private lateinit var viewModel: SignOutViewModel
    private lateinit var dialogView : View
    private lateinit var binding : DialogFragmentSignoutBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return LayoutInflater.from(requireContext()).inflate(
            R.layout.dialog_fragment_signout,
            null
        )
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {

        binding = DialogFragmentSignoutBinding.bind(requireView())

        dialogView = LayoutInflater.from(requireContext()).inflate(
            R.layout.dialog_fragment_signout,
            null
        )
        return MaterialAlertDialogBuilder(requireContext())
            .setView(dialogView)
            .create()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(SignOutViewModel::class.java)
        subscribeToObserve()

        binding = DialogFragmentSignoutBinding.inflate(layoutInflater)
        binding.positiveButtonDialog.setOnClickListener {
            viewModel.onYesClick()
        }
    }

    private fun subscribeToObserve(){
        binding = DialogFragmentSignoutBinding.inflate(layoutInflater)
        viewModel = ViewModelProvider(requireActivity()).get(SignOutViewModel::class.java)
        viewModel.signOutStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.signOutProgressbar.isVisible = false
                binding.positiveButtonDialog.isEnabled = true
                snackbar(it)
            },
            onLoading = {
                binding.signOutProgressbar.isVisible = true
                binding.positiveButtonDialog.isEnabled = false
            }
        ){
            binding.signOutProgressbar.isVisible = false
            binding.positiveButtonDialog.isEnabled = true
            when(it){
                true -> {
                    snackbar("Signed Out successfully")
                    Intent(activity, AuthActivity::class.java).also { intent ->
                        startActivity(intent)
                        activity?.finish()
                        activity?.finishAffinity()
                    }
                }
                false -> snackbar("Something went wrong")
            }
        })
    }

}