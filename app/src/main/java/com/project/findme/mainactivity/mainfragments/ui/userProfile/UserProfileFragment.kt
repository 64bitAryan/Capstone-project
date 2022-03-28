package com.project.findme.mainactivity.mainfragments.ui.userProfile

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.RequestManager
import com.google.firebase.auth.FirebaseAuth
import com.project.findme.adapter.PostAdapter
import com.project.findme.adapter.PostAdapterProfile
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(UserProfileViewModel::class.java)
        subscribeToObserve()

        binding = FragmentUserProfileBinding.bind(view)

        FirebaseAuth.getInstance().currentUser?.uid?.let { viewModel.updateUI(it) }
        FirebaseAuth.getInstance().currentUser?.uid?.let { viewModel.getPost(it) }

        setUpRecyclerView()

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
}