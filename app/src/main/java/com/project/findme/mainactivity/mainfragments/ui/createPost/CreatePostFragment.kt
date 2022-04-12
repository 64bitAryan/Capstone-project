package com.project.findme.mainactivity.mainfragments.ui.createPost

import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.canhub.cropper.*
import com.project.findme.utils.EventObserver
import com.project.findme.utils.snackbar
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentCreatepostScreenBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreatePostFragment : Fragment(R.layout.fragment_createpost_screen) {

    @Inject
    lateinit var glide: RequestManager
    private val viewModel: CreatePostViewModel by viewModels()
    private lateinit var curImageUri: Uri
    private lateinit var binding: FragmentCreatepostScreenBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCreatepostScreenBinding.bind(view)
        subscribeToObserve()

        binding.apply {
            addImageBt.setOnClickListener {
                startCrop()
            }
            imageIv.setOnClickListener {
                startCrop()
            }
            createPostBt.setOnClickListener {
                curImageUri.let { uri ->
                    viewModel.createPost(
                        uri,
                        titleEt.text.toString(),
                        descriptionEt.text.toString()
                    )
                }
            }
        }
    }

    private val cropImage = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            curImageUri = result.uriContent!!
            viewModel.setCurrentImageUri(curImageUri)
        } else {
            val exception = result.error
            snackbar(exception.toString())
        }
    }

    private fun startCrop() {
        cropImage.launch(
            options {
                setGuidelines(CropImageView.Guidelines.ON)
                setAspectRatio(1, 1)
            }
        )
    }


    private fun subscribeToObserve() {

        viewModel.curImageUri.observe(viewLifecycleOwner) {
            curImageUri = it
            binding.apply {
                addImageBt.isVisible = false
                glide.load(curImageUri).into(imageIv)
                imageIv.isVisible = true
            }
        }

        viewModel.createPostStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.apply {
                    showProgress(false)
                }
                snackbar(it)
            },
            onLoading = {
                binding.apply {
                    showProgress(true)
                }
            }
        ) {
            binding.apply {
                showProgress(false)
            }
            findNavController().popBackStack()
        })
    }

    private fun showProgress(bool: Boolean) {
        binding.apply {
            cvProgressPost.isVisible = bool
            if (bool) {
                parentLayoutPost.alpha = 0.5f
                activity?.window!!.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            } else {
                parentLayoutPost.alpha = 1f
                activity?.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
    }

}