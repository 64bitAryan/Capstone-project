package com.project.findme.auth.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentRegisterUserBinding

class RegisterFragment : Fragment(R.layout.fragment_register_user) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentRegisterUserBinding.bind(view)
        binding.apply {

        }
    }
}