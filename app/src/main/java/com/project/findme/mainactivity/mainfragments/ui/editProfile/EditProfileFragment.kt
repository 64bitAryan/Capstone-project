package com.project.findme.mainactivity.mainfragments.ui.editProfile

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.google.android.material.chip.Chip
import com.google.firebase.auth.FirebaseAuth
import com.project.findme.data.entity.UpdateCredentials
import com.project.findme.data.entity.UpdateUser
import com.project.findme.data.entity.User
import com.project.findme.utils.Constants
import com.project.findme.utils.EventObserver
import com.project.findme.utils.snackbar
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentEditProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class EditProfileFragment : Fragment(R.layout.fragment_edit_profile) {

    @Inject
    lateinit var glide: RequestManager
    private val viewModel: EditProfileViewModel by viewModels()
    private lateinit var binding: FragmentEditProfileBinding
    private var interests = mutableSetOf<String>()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentEditProfileBinding.bind(view)
        subscribeToObserver()
        FirebaseAuth.getInstance().currentUser?.let { viewModel.getUserProfile(it.uid) }

        val adapter: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            Constants.professions
        )

        val adapterHobbies: ArrayAdapter<String> = ArrayAdapter<String>(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            Constants.hobbies
        )

        binding.apply {

            etProfessionEditProfile.threshold = 1
            etProfessionEditProfile.setAdapter(adapter)

            etInterestsEditProfile.threshold = 1
            etInterestsEditProfile.setAdapter(adapterHobbies)

            addBt.setOnClickListener {
                if (etInterestsEditProfile.text.isNotEmpty()) {
                    addChipToGroup(etInterestsEditProfile.text.toString())
                }
            }

            btnUpdateProfile.setOnClickListener {
                viewModel.updateProfile(viewToObject())
            }
        }
    }

    private fun viewToObject(): UpdateUser {
        var user: UpdateUser
        binding.apply {
            val inte = interests.toList()
            val name = etUsernameEditProfile.text.toString()
            val description = etDescriptionEditProfile.text.toString()
            val profession = etProfessionEditProfile.text.toString()
            val uid = FirebaseAuth.getInstance().uid!!
            val cred = UpdateCredentials(profession, inte)
            user = UpdateUser(uid, name, description, cred)
        }
        return user
    }

    private fun loadToViews(user: User) {
        binding.apply {
            val cred = user.credential
            etUsernameEditProfile.setText(user.userName)
            etDescriptionEditProfile.setText(user.description)
            etProfessionEditProfile.setText(cred.profession)
            glide.load(user.profilePicture).into(ivProfilePictureEditUser)
            interests = cred.interest.toMutableSet()
            for (interest in interests)
                addChipToGroup(interest)
        }
    }

    private fun addChipToGroup(interest: String) {
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

    private fun subscribeToObserver() {
        viewModel.userProfileStatus.observe(viewLifecycleOwner, EventObserver(
            onError = { error ->
                showProgress(false)
                snackbar(error)
            },
            onLoading = {
                showProgress(true)
            }
        ) { user ->
            showProgress(false)
            loadToViews(user)
        })

        viewModel.updateProfileStatus.observe(viewLifecycleOwner, EventObserver(
            onError = { error ->
                binding.apply {
                    showProgress(false)
                    snackbar(error)
                }
            },
            onLoading = {
                showProgress(true)
            }
        ) {
            showProgress(false)
            findNavController().navigate(R.id.action_editProfileFragment_to_userProfileFragment)
            snackbar("Updated Successfully")
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