package com.project.findme

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentSplashScreenBinding

class SplashScreenFragment : Fragment(R.layout.fragment_splash_screen) {
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val binding = FragmentSplashScreenBinding.bind(view)
        binding.apply {

        }
    }
}