package com.project.findme.mainactivity.mainfragments.ui.createPost

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.RequestManager
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageView
import com.canhub.cropper.options
import com.project.findme.data.entity.Post
import com.project.findme.utils.EventObserver
import com.project.findme.utils.hideKeyboard
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
    private var curImageUri: Uri = Uri.EMPTY
    private lateinit var binding: FragmentCreatepostScreenBinding
    private val args: CreatePostFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val callback: OnBackPressedCallback =
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showConfirmationDialog()
                }
            }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

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
            titleEt.setText(args.title)
            descriptionEt.setText(args.description)
            if (args.imageUrl != "") {
                addImageBt.isVisible = false
                glide.load(args.imageUrl).into(imageIv)
                imageIv.isVisible = true
            }

            createPostBt.setOnClickListener {
                hideKeyboard(requireActivity())
                curImageUri.let { uri ->
                    viewModel.createPost(
                        uri,
                        titleEt.text.toString(),
                        descriptionEt.text.toString(),
                        args.postId,
                        args.imageUrl
                    )
                }
            }

            saveDraftBt.setOnClickListener {
                hideKeyboard(requireActivity())
                if (args.postId == "") {
                    viewModel.createDraftPost(
                        curImageUri,
                        titleEt.text.toString().trim(),
                        descriptionEt.text.toString().trim()
                    )
                } else {
                    viewModel.updateDraftPost(
                        curImageUri,
                        binding.titleEt.text.toString(),
                        binding.descriptionEt.text.toString(),
                        args.postId,
                        args.imageUrl
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

    private fun showConfirmationDialog() {
        if (binding.titleEt.text.trim()
                .isEmpty() && binding.descriptionEt.text.trim()
                .isEmpty() && curImageUri == Uri.EMPTY
        ) {
            findNavController().navigateUp()
        } else if (binding.titleEt.text.toString()
                .trim() == args.title && binding.descriptionEt.text.toString()
                .trim() == args.description && curImageUri.toString() == args.imageUrl
        ) {
            findNavController().navigateUp()
        } else if (args.postId != "") {
            AlertDialog.Builder(requireContext())
                .setTitle("Go Back?")
                .setMessage("Want to save this draft?")
                .setPositiveButton(
                    "Yes"
                ) { _, _ ->
                    viewModel.updateDraftPost(
                        curImageUri,
                        binding.titleEt.text.toString(),
                        binding.descriptionEt.text.toString(),
                        args.postId,
                        args.imageUrl
                    )
                }
                .setNegativeButton("No") { _, _ ->
                    findNavController().navigateUp()
                }
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        } else {
            AlertDialog.Builder(requireContext())
                .setTitle("Go Back?")
                .setMessage("Are you sure you want to go back? You will lose the data, instead draft the post?")
                .setPositiveButton(
                    "Yes"
                ) { _, _ ->
                    viewModel.createDraftPost(
                        curImageUri,
                        binding.titleEt.text.toString(),
                        binding.descriptionEt.text.toString()
                    )
                }
                .setNegativeButton("No") { _, _ ->
                    findNavController().navigateUp()
                }
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }
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