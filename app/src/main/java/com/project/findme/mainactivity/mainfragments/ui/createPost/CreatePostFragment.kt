package com.project.findme.mainactivity.mainfragments.ui.createPost

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContract
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.RequestManager
import com.project.findme.utils.EventObserver
import com.project.findme.utils.snackbar
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentCreatepostScreenBinding
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CreatePostFragment: Fragment(R.layout.fragment_createpost_screen) {

    @Inject
    lateinit var glide: RequestManager
    private val viewModel: CreatePostViewModel by viewModels()
    private lateinit var cropContent: ActivityResultLauncher<String>
    private var curImageUri: Uri? = null
    private lateinit var binding: FragmentCreatepostScreenBinding

    private val cropActivityResultContract = object: ActivityResultContract<String, Uri?>() {
        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return CropImage.getActivityResult(intent)?.uri
        }

        override fun createIntent(context: Context, input: String): Intent {
            return CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .getIntent(requireContext())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        cropContent = registerForActivityResult(cropActivityResultContract) {
            it?.let {
                viewModel.setCurrentImageUri(it)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentCreatepostScreenBinding.bind(view)
        subscribeToObserve()
        binding.apply {
            addImageBt.setOnClickListener {
                cropContent.launch("image/*")
            }
            imageIv.setOnClickListener {
                cropContent.launch("image/*")
            }
            createPostBt.setOnClickListener {
                curImageUri?.let{ uri ->
                    viewModel.createPost(uri, titleEt.text.toString(), descriptionEt.text.toString())
                } ?: snackbar(getString(R.string.error_no_image_chosen))
            }
        }
    }

    private fun subscribeToObserve() {

        viewModel.curImageUri.observe(viewLifecycleOwner){
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
        ){
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