package com.project.findme.mainactivity.mainfragments.ui.searchedProfile

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.RequestManager
import com.project.findme.adapter.PostAdapterProfile
import com.project.findme.data.entity.User
import com.project.findme.utils.EventObserver
import com.project.findme.utils.snackbar
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentSearchedProfileBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SearchedProfileFragment : Fragment(R.layout.fragment_searched_profile) {

    @Inject
    lateinit var postAdapter: PostAdapterProfile

    @Inject
    lateinit var glide: RequestManager
    private lateinit var viewModel: SearchedProfileViewModel
    private lateinit var binding: FragmentSearchedProfileBinding
    private val args: SearchedProfileFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity()).get(SearchedProfileViewModel::class.java)
        subscribeToObserve()

        binding = FragmentSearchedProfileBinding.bind(view)

        viewModel.updateUI(args.uid)
        viewModel.getPost(args.uid)

        setUpRecyclerView()

        binding.apply {

            swipeRefreshLayoutSearchedProfile.setOnRefreshListener {
                viewModel.updateUI(args.uid)
                viewModel.getPost(args.uid)
            }

            btnFollowUser.setOnClickListener {

            }

            btnMessageUser.setOnClickListener {

            }
        }

    }

    private fun subscribeToObserve() {

        viewModel.searchedProfileStatus.observe(viewLifecycleOwner, EventObserver(

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
        binding.postRvSearchedProfile.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = postAdapter
        }
    }

    private fun showProgress(bool: Boolean) {
        binding.apply {
            swipeRefreshLayoutSearchedProfile.isRefreshing = bool
            if (bool) {
                parentLayoutSearchedProfile.alpha = 0.5f
                activity?.window!!.setFlags(
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
                )
            } else {
                parentLayoutSearchedProfile.alpha = 1f
                activity?.window!!.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(user: User) {
        binding.apply {
            tvNameSearchedProfile.text = user.userName
            tvDescriptionSearchedProfile.text = user.description
            tvFollowersSearchedProfile.text = user.follows.size.toString() + " Followers"
            tvFollowingsSearchedProfile.text = user.followings.size.toString() + " Followings"
            glide.load(user.profilePicture).into(ivProfilePictureSearchedProfile)
        }
    }
}