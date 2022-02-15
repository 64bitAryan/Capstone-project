package com.project.findme.mainactivity.mainfragments.ui.userProfile

import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentUserProfileBinding

class UserProfileFragment: Fragment(R.layout.fragment_user_profile) {

    private lateinit var viewModel: UserProfileViewModel
    private lateinit var binding: FragmentUserProfileBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(UserProfileViewModel::class.java)

        binding = FragmentUserProfileBinding.bind(view)
        binding.apply {

            ivEditProfile.setOnClickListener {
                findNavController().navigate(
                    UserProfileFragmentDirections.actionUserProfileFragmentToEditProfileFragment()
                )
            }
        }
    }

    private fun showProgress(bool: Boolean) {
        binding.apply {
            cvProgressProfile.isVisible = bool
            if (bool) {
                parentLayoutProfile.alpha = 0.5f
                activity?.window!!.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            } else {
                parentLayoutProfile.alpha = 1f
                activity?.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
    }

}