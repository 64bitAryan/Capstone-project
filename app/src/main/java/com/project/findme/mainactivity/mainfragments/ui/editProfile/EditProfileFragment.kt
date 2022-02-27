package com.project.findme.mainactivity.mainfragments.ui.editProfile

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.project.findme.data.entity.User
import com.project.findme.utils.Constants.hobbies
import com.project.findme.utils.Constants.professions
import com.project.findme.utils.EventObserver
import com.project.findme.utils.hideKeyboard
import com.project.findme.utils.snackbar
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentEditProfileBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private lateinit var viewModel: EditProfileViewModel
    private lateinit var binding: FragmentEditProfileBinding
    private var interests = mutableSetOf<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(EditProfileViewModel::class.java)

        binding = FragmentEditProfileBinding.bind(view)

        FirebaseAuth.getInstance().currentUser?.uid?.let { viewModel.updateUI(it) }
        subscribeToObserve()

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            professions
        )

        val adapterHobbies: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            hobbies
        )

        binding.apply {

            etProfessionEditProfile.threshold = 1
            etProfessionEditProfile.setAdapter(adapter)

            etInterestsEditProfile.threshold = 1
            etInterestsEditProfile.setAdapter(adapterHobbies)

            etUsernameEditProfile.addTextChangedListener {
                viewModel.username = it.toString()
            }

            etDescriptionEditProfile.addTextChangedListener {
                viewModel.description = it.toString()
            }

            etProfessionEditProfile.addTextChangedListener {
                viewModel.profession = it.toString()
            }

            addBt.setOnClickListener {
                val interest: String = etInterestsEditProfile.text.toString()
                addChipToGroup(requireContext(), interest)
            }

            btnUpdateProfile.setOnClickListener {
                hideKeyboard(activity as Activity)
                viewModel.updateProfile(interests.toList())
            }

        }
    }

    private fun addChipToGroup(context: Context, interest: String) {

        val chip = Chip(context).apply {
            id = View.generateViewId()
            text = interest
            isClickable = true
            isCheckable = false
            isCloseIconVisible = true
        }

        interests.add(interest)

        binding.editProfileHobbiesCg.addView(chip)

        chip.setOnCloseIconClickListener {
            interests.remove(chip.text.toString())
            binding.editProfileHobbiesCg.removeView(chip)
        }

        binding.etInterestsEditProfile.text?.clear()
    }

    private fun subscribeToObserve() {

        viewModel.updateUIStatus.observe(viewLifecycleOwner, EventObserver(

            onError = {
                showProgress(false)
                snackbar(it)
            },
            onLoading = {
                showProgress(true)
            }
        ) { user ->
            updateUI(user)
            showProgress(false)
        })

        viewModel.updateProfileStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                showProgress(false)
                snackbar(it)
            },
            onLoading = {
                showProgress(true)
            }
        ) {
            showProgress(false)
            snackbar("User profile updated successfully!")
            findNavController().popBackStack()
        })

    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(user: User) {
        binding.apply {
            editProfileHobbiesCg.removeAllViews()
            etUsernameEditProfile.setText(user.userName)
            etDescriptionEditProfile.setText(user.description)
            etProfessionEditProfile.setText(user.credential.profession)
            for (interest in user.credential.interest) {
                addChipToGroup(requireContext(), interest)
            }
        }
    }

    private fun showProgress(bool: Boolean) {
        binding.apply {
            cvProgressEditProfile.isVisible = bool
            if (bool) {
                parentLayoutEditProfile.alpha = 0.5f
                activity?.window!!.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            } else {
                parentLayoutEditProfile.alpha = 1f
                activity?.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        interests = mutableSetOf()
    }

}