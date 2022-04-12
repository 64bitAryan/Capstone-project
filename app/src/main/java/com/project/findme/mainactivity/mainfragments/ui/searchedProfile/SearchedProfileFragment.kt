package com.project.findme.mainactivity.mainfragments.ui.searchedProfile

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
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.project.findme.adapter.PostAdapterProfile
import com.project.findme.data.entity.Post
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
    private lateinit var builder: Dialog

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        builder = Dialog(requireContext())

        builder.requestWindowFeature(Window.FEATURE_NO_TITLE)
        builder.window?.setBackgroundDrawable(
            ColorDrawable(Color.TRANSPARENT)
        )

        viewModel = ViewModelProvider(requireActivity())[SearchedProfileViewModel::class.java]
        subscribeToObserve()

        binding = FragmentSearchedProfileBinding.bind(view)

        viewModel.updateUI(args.uid)
        viewModel.getPost(args.uid)

        setUpRecyclerView()

        postAdapter.setOnItemClickListener {
            publicationQuickView(it)
        }

        binding.apply {

            swipeRefreshLayoutSearchedProfile.setOnRefreshListener {
                viewModel.updateUI(args.uid)
                viewModel.getPost(args.uid)
            }

            btnFollowUser.setOnClickListener {
                viewModel.followUser(args.uid)
            }

            btnUnfollowUser.setOnClickListener {
                viewModel.unFollowUser(args.uid)
            }

            btnMessageUser.setOnClickListener {
                findNavController().navigate(SearchedProfileFragmentDirections.actionSearchedProfileFragmentToChatFragment())
            }

            tvFollowersSearchedProfile.setOnClickListener {
                findNavController().navigate(
                    SearchedProfileFragmentDirections.actionSearchedProfileFragmentToListFollowersFragment(
                        uid = args.uid,
                        type = "Followers",
                        username = args.username
                    )
                )
            }

            tvFollowingsSearchedProfile.setOnClickListener {
                findNavController().navigate(
                    SearchedProfileFragmentDirections.actionSearchedProfileFragmentToListFollowersFragment(
                        uid = args.uid,
                        type = "Followings",
                        username = args.username
                    )
                )
            }

            postRvSearchedProfile.addOnItemTouchListener(object : RecyclerView.OnItemTouchListener {
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

    @SuppressLint("SetTextI18n")
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
            showProgress(false)
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
            tvFollowersSearchedProfile.text = user.follows.size.toString() + "\nFollowers"
            tvFollowingsSearchedProfile.text = user.followings.size.toString() + "\nFollowings"
            glide.load(user.profilePicture).into(ivProfilePictureSearchedProfile)
            if (Firebase.auth.currentUser?.uid!! in user.follows) {
                btnFollowUser.visibility = View.INVISIBLE
                btnUnfollowUser.visibility = View.VISIBLE
            } else{
                btnFollowUser.visibility = View.VISIBLE
                btnUnfollowUser.visibility = View.INVISIBLE
            }
        }
    }

    private fun publicationQuickView(post: Post) {
        val view: View = layoutInflater.inflate(R.layout.item_post_profile, null)
        val postImage: ImageView = view.findViewById<View>(R.id.post_iv) as ImageView
        glide.load(post.imageUrl).into(postImage)
        builder.setContentView(view)
        val width = (resources.displayMetrics.widthPixels * 0.90).toInt()
        val height = (resources.displayMetrics.heightPixels * 0.60).toInt()
        builder.window?.setLayout(width, height)
        builder.show()
    }

    fun hideQuickView() {
        builder.dismiss()
    }
}