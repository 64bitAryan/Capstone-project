package com.project.findme.mainactivity.mainfragments.ui.createTextPost

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.common.collect.BiMap
import com.project.findme.data.entity.Post
import com.project.findme.utils.EventObserver
import com.project.findme.utils.hideKeyboard
import com.project.findme.utils.snackbar
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentCreateTextPostScreenBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream


@AndroidEntryPoint
class CreateTextPostFragment : Fragment(R.layout.fragment_create_text_post_screen) {

    private val viewModel: CreateTextPostViewModel by viewModels()
    private lateinit var binding: FragmentCreateTextPostScreenBinding
    private lateinit var bitmap: Bitmap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showBackPressedConfirmation()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCreateTextPostScreenBinding.bind(view)
        subscribeToObserve()

        binding.apply {
            createImageBt.setOnClickListener {
                hideKeyboard(requireActivity())
                viewModel.createImage(etTextPost.text.toString().trim())
            }

            ivTextPost.setOnClickListener {
                showConfirmationDialog()
            }

            createPostBt.setOnClickListener {
                val bytes = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                val path: String = MediaStore.Images.Media.insertImage(
                    requireContext().contentResolver,
                    bitmap,
                    "Image",
                    null
                )
                val uri = Uri.parse(path)
                viewModel.createPost(uri)
            }
        }
    }

    private fun showBackPressedConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Go Back?")
            .setPositiveButton(
                "Change Text"
            ) { _, _ ->
                binding.etTextPost.isVisible = true
                binding.createImageBt.isVisible = true
                binding.ivTextPost.isVisible = false
                binding.createPostBt.isVisible = false
            }
            .setNegativeButton("Go back") { _, _ ->
                findNavController().navigateUp()
            }
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    private fun showConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Change Text?")
            .setPositiveButton(
                "Yes"
            ) { _, _ ->
                binding.etTextPost.isVisible = true
                binding.createImageBt.isVisible = true
                binding.ivTextPost.isVisible = false
                binding.createPostBt.isVisible = false
            }
            .setNegativeButton("No", null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    private fun subscribeToObserve() {

        viewModel.createImageStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.progressBarTextPost.isVisible = false
                snackbar(it)
            },
            onLoading = {
                binding.progressBarTextPost.isVisible = true
            }
        ) {
            bitmap = it
            binding.progressBarTextPost.isVisible = false
            binding.ivTextPost.setImageBitmap(it)
            binding.etTextPost.isVisible = false
            binding.createImageBt.isVisible = false
            binding.ivTextPost.isVisible = true
            binding.createPostBt.isVisible = true
        })

        viewModel.createPostStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.progressBarTextPost.isVisible = false
                snackbar(it)
            },
            onLoading = {
                binding.progressBarTextPost.isVisible = true
            }
        ) {
            binding.progressBarTextPost.isVisible = false
            findNavController().navigateUp()
        })
    }
}