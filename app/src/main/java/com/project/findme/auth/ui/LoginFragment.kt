package com.project.findme.auth.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentLoginUserBinding

class LoginFragment : Fragment(R.layout.fragment_login_user) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentLoginUserBinding.bind(view)

        binding.apply {

        }
    }
}