package com.project.findme.mainactivity.mainfragments.ui.editProfile

import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.chip.Chip
import com.project.findme.utils.Constants.hobbies
import com.project.findme.utils.Constants.professions
import com.project.findme.utils.EventObserver
import com.project.findme.utils.hideKeyboard
import com.project.findme.utils.snackbar
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentEditProfileBinding

class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    private lateinit var viewModel: EditProfileViewModel
    private lateinit var binding: FragmentEditProfileBinding
    private var interests = mutableSetOf<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(EditProfileViewModel::class.java)
        subscribeToObserve()

        binding = FragmentEditProfileBinding.bind(view)

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

            etUsernameEditProfile.setText(viewModel.username)
            etDescriptionEditProfile.setText(viewModel.description)
            etProfessionEditProfile.setText(viewModel.profession)

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
            when (it) {
                true -> {
                    snackbar("User profile updated successfully!")
                }
                false -> snackbar("Error occurred!")
            }
        })
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

}