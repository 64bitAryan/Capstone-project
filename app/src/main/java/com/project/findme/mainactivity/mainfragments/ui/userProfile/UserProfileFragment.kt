package com.project.findme.mainactivity.mainfragments.ui.userProfile

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.google.firebase.auth.FirebaseAuth
import com.project.findme.adapter.PostAdapterProfile
import com.project.findme.data.entity.Post
import com.project.findme.data.entity.User
import com.project.findme.utils.EventObserver
import com.project.findme.utils.snackbar
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentUserProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class UserProfileFragment : Fragment(R.layout.fragment_user_profile) {

    @Inject
    lateinit var postAdapter: PostAdapterProfile

    @Inject
    lateinit var glide: RequestManager
    private lateinit var viewModel: UserProfileViewModel
    private lateinit var binding: FragmentUserProfileBinding
    private lateinit var builder: Dialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(UserProfileViewModel::class.java)
        subscribeToObserve()

        binding = FragmentUserProfileBinding.bind(view)

        FirebaseAuth.getInstance().currentUser?.uid?.let { viewModel.updateUI(it) }
        FirebaseAuth.getInstance().currentUser?.uid?.let { viewModel.getPost(it) }

        setUpRecyclerView()

        postAdapter.setOnItemClickListener {
            publicationQuickView(it)
        }

        binding.apply {

            ivEditProfile.setOnClickListener {
                findNavController().navigate(
                    UserProfileFragmentDirections.actionUserProfileFragmentToEditProfileFragment()
                )
            }

            tvFollowersUserProfile.setOnClickListener {
                findNavController().navigate(
                    UserProfileFragmentDirections.actionUserProfileFragmentToListFollowersFragmentUser(
                        type = "Followers",
                        username = FirebaseAuth.getInstance().currentUser?.displayName!!
                    )
                )
            }

            tvFollowingsUserProfile.setOnClickListener {
                findNavController().navigate(
                    UserProfileFragmentDirections.actionUserProfileFragmentToListFollowersFragmentUser(
                        type = "Followings",
                        username = FirebaseAuth.getInstance().currentUser?.displayName!!
                    )
                )
            }

            swipeRefreshLayoutProfile.setOnRefreshListener {
                FirebaseAuth.getInstance().currentUser?.uid?.let { viewModel.updateUI(it) }
                FirebaseAuth.getInstance().currentUser?.uid?.let { viewModel.getPost(it) }
            }

            postRvProfile.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
                override fun onInterceptTouchEvent(rv: RecyclerView, e: MotionEvent): Boolean {
                    if (e.action == MotionEvent.ACTION_UP) {
                        hideQuickView()
                    }
                    return false
                }

                override fun onTouchEvent(rv: RecyclerView, event: MotionEvent) {}
                override fun onRequestDisallowInterceptTouchEvent(disallowIntercept: Boolean) {}
            })
        }

    }

    private fun subscribeToObserve() {

        viewModel.userProfileStatus.observe(viewLifecycleOwner, EventObserver(

            onError = {
                showProgress(false)
                snackbar(it)
            },
            onLoading = {
                showProgress(true)
            }
        ) { user ->
            updateUI(user)
        })

        viewModel.post.observe(viewLifecycleOwner, EventObserver(
            onError = {
                showProgress(false)
                snackbar(it)
            },
            onLoading = {
                showProgress(true)
                postAdapter.posts = listOf()
            }
        ) { postList ->
            postAdapter.posts = postList
            showProgress(false)
        })
    }

    private fun setUpRecyclerView() {
        binding.postRvProfile.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = postAdapter
        }
    }

    private fun showProgress(bool: Boolean) {
        binding.apply {
            swipeRefreshLayoutProfile.isRefreshing = bool
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

    @SuppressLint("SetTextI18n")
    private fun updateUI(user: User) {
        binding.apply {
            tvNameUserProfile.text = user.userName
            tvDescriptionUserProfile.text = user.description
            tvFollowersUserProfile.text = user.follows.size.toString() + "\nFollowers"
            tvFollowingsUserProfile.text = user.followings.size.toString() + "\nFollowings"
            glide.load(user.profilePicture).into(ivProfilePictureUserProfile)
        }
    }

    private fun publicationQuickView(post: Post) {
        val view: View = layoutInflater.inflate(R.layout.item_post_profile, null)
        val postImage: ImageView = view.findViewById<View>(R.id.post_iv) as ImageView
        glide.load(post.imageUrl).into(postImage)
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE)
        builder.window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )
        builder.setContentView(view)
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.60).toInt()
        builder.window?.setLayout(width, height)
        builder.show()
    }

    fun hideQuickView() {
        builder = Dialog(requireContext())
        builder.dismiss()
    }
}