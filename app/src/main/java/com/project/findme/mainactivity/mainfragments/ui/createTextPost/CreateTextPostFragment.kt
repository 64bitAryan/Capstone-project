package com.project.findme.mainactivity.mainfragments.ui.createTextPost

import android.content.ContentValues
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import android.view.WindowManager
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.project.findme.utils.Constants
import com.project.findme.utils.EventObserver
import com.project.findme.utils.snackbar
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentCreateTextPostScreenBinding
import dagger.hilt.android.AndroidEntryPoint
import java.io.ByteArrayOutputStream
import java.io.OutputStream


@AndroidEntryPoint
class CreateTextPostFragment : Fragment(R.layout.fragment_create_text_post_screen) {

    private val viewModel: CreateTextPostViewModel by viewModels()
    private lateinit var binding: FragmentCreateTextPostScreenBinding
    private lateinit var bitmap: Bitmap
    private var color = 0
    private var penColor = 0

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

            etTextPost.addTextChangedListener {
                if (etTextPost.layout.lineCount > 7) {
                    etTextPost.text.delete(etTextPost.text.length - 1, etTextPost.text.length)
                }
                viewModel.addTextToImage(it.toString().trim(), color, penColor)
            }

            btnBlue.setOnClickListener {
                ivTextPost.setBackgroundResource(R.drawable.blue_background)
                color = 0
                viewModel.addTextToImage(etTextPost.text.toString().trim(), color, penColor)
            }

            btnRed.setOnClickListener {
                ivTextPost.setBackgroundResource(R.drawable.red_background)
                color = 1
                viewModel.addTextToImage(etTextPost.text.toString().trim(), color, penColor)
            }

            btnPeach.setOnClickListener {
                ivTextPost.setBackgroundResource(R.drawable.peach_background)
                color = 2
                viewModel.addTextToImage(etTextPost.text.toString().trim(), color, penColor)
            }

            btnYellow.setOnClickListener {
                ivTextPost.setBackgroundResource(R.drawable.yellow_background)
                color = 3
                viewModel.addTextToImage(etTextPost.text.toString().trim(), color, penColor)
            }

            btnBrown.setOnClickListener {
                ivTextPost.setBackgroundResource(R.drawable.brown_background)
                color = 4
                viewModel.addTextToImage(etTextPost.text.toString().trim(), color, penColor)
            }

            btnRedPen.setOnClickListener {
                penColor = 0
                viewModel.addTextToImage(etTextPost.text.toString().trim(), color, penColor)
            }

            btnCyanPen.setOnClickListener {
                penColor = 1
                viewModel.addTextToImage(etTextPost.text.toString().trim(), color, penColor)
            }

            btnBluePen.setOnClickListener {
                penColor = 2
                viewModel.addTextToImage(etTextPost.text.toString().trim(), color, penColor)
            }

            btnPurplePen.setOnClickListener {
                penColor = 3
                viewModel.addTextToImage(etTextPost.text.toString().trim(), color, penColor)
            }

            btnYellowPen.setOnClickListener {
                penColor = 4
                viewModel.addTextToImage(etTextPost.text.toString().trim(), color, penColor)
            }

            btnWhitePen.setOnClickListener {
                penColor = 5
                viewModel.addTextToImage(etTextPost.text.toString().trim(), color, penColor)
            }

            createPostBt.setOnClickListener {
                val filename = "IMG_${System.currentTimeMillis()}.jpg"
                var fos: OutputStream?
                var imageUri: Uri
                val contentValues = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
                    put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
                        put(MediaStore.Video.Media.IS_PENDING, 1)
                    }
                }

                val contentResolver = requireContext().contentResolver

                contentResolver.also { resolver ->
                    imageUri =
                        resolver.insert(
                            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                            contentValues
                        )!!
                    fos = imageUri.let { resolver.openOutputStream(it) }


                    fos?.use { bitmap.compress(Bitmap.CompressFormat.JPEG, 70, it) }

                    contentValues.clear()
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                        contentValues.put(MediaStore.Video.Media.IS_PENDING, 0)
                    }
                    resolver.update(imageUri, contentValues, null, null)
                }

                viewModel.createPost(imageUri)
            }
        }
    }

    private fun showBackPressedConfirmation() {
        if (binding.etTextPost.text.toString().trim() == "") {
            findNavController().navigateUp()
        } else {
            AlertDialog.Builder(requireContext())
                .setTitle("Go Back?")
                .setMessage("Are you sure you want to go back? You will lose the post!")
                .setPositiveButton(
                    "No"
                ) { _, _ ->

                }
                .setNegativeButton("Go back") { _, _ ->
                    findNavController().navigateUp()
                }
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show()
        }
    }

    private fun subscribeToObserve() {

        viewModel.createImageStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                snackbar(it)
            },
            onLoading = {
            }
        ) {
            bitmap = it
            binding.ivTextPost.setImageBitmap(it)
        })

        viewModel.createPostStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                showProgress(false)
                snackbar(it)
            },
            onLoading = {
                showProgress(true)
            }
        ) {
            showProgress(false)
            val bundle = Bundle()
            bundle.putString(Constants.FRAGMENT_ARG_KEY, "CreatePost")
            findNavController().navigate(
                R.id.homeFragment, bundle
            )
        })
    }

    private fun showProgress(bool: Boolean) {
        binding.apply {
            cvProgressTextPost.isVisible = bool
            if (bool) {
                parentLayoutTextPost.alpha = 0.5f
                activity?.window!!.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            } else {
                parentLayoutTextPost.alpha = 1f
                activity?.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
    }
}