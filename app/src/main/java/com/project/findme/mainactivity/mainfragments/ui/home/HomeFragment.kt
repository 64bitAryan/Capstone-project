package com.project.findme.mainactivity.mainfragments.ui.home

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.project.findme.adapter.PostAdapter
import com.project.findme.data.entity.Post
import com.project.findme.utils.Constants
import com.project.findme.utils.EventObserver
import com.project.findme.utils.snackbar
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentHomeScreenBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home_screen) {
    @Inject
    lateinit var postAdapter: PostAdapter
    val viewModel: HomeViewModel by viewModels()
    private lateinit var binding: FragmentHomeScreenBinding
    private lateinit var args: String

    private var isFabOpen = false
    private lateinit var fabOpen: Animation
    private lateinit var fabClose: Animation
    private lateinit var rotateForward: Animation
    private lateinit var rotateBackward: Animation

    var index = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        FirebaseAuth.getInstance().currentUser?.let { viewModel.getPost(it.uid) }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeScreenBinding.bind(view)

        subscribeToObserver()
        setUpRecyclerView()

        arguments?.takeIf { it.containsKey(Constants.FRAGMENT_ARG_KEY) }?.apply {
            args = getString(Constants.FRAGMENT_ARG_KEY)!!
            if (args == "CreatePost") {
                FirebaseAuth.getInstance().currentUser?.let { viewModel.getPost(it.uid) }
                requireArguments().clear()
            }
        }

        fabOpen = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_open)
        fabClose = AnimationUtils.loadAnimation(requireContext(), R.anim.fab_close)

        rotateForward = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_forward)
        rotateBackward = AnimationUtils.loadAnimation(requireContext(), R.anim.rotate_backward)

        binding.apply {
            createPostFb.setOnClickListener {
                animateFAB()
            }

            fabCreateImagePost.setOnClickListener {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToCreatePostFragment(
                        "",
                        "",
                        "",
                        ""
                    )
                )
            }

            fabCreateTextPost.setOnClickListener {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToCreateTextPostFragment()
                )
            }

            swipeRefreshLayout.setOnRefreshListener {
                FirebaseAuth.getInstance().currentUser?.let { viewModel.getPost(it.uid) }
            }
            postRv.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    if (dy > 10 && createPostFb.isShown) {
                        createPostFb.hide()
                        if (isFabOpen) {
                            animateFAB()
                        }
                    }
                    if (dy < -10 && !createPostFb.isShown) {
                        createPostFb.show()
                    }
                    if (!recyclerView.canScrollVertically(-1)) {
                        createPostFb.show()
                    }
                }
            })
        }
    }

    private fun subscribeToObserver() {
        viewModel.post.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.swipeRefreshLayout.isRefreshing = false
                snackbar(it)
            },
            onLoading = {
                binding.swipeRefreshLayout.isRefreshing = true
                postAdapter.posts = listOf()
            }
        ) { postList ->
            postAdapter.posts = postList
            binding.swipeRefreshLayout.isRefreshing = false
        })

        viewModel.deletePostStatus.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.swipeRefreshLayout.isRefreshing = false
                snackbar(it)
            },
            onLoading = {
                binding.swipeRefreshLayout.isRefreshing = true
            }
        ) { deletedPost ->
            postAdapter.posts -= deletedPost
            binding.swipeRefreshLayout.isRefreshing = false
        })

        viewModel.like.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.swipeRefreshLayout.isRefreshing = false
                snackbar(it)
            },
            onLoading = {
                binding.swipeRefreshLayout.isRefreshing = true
            }
        ) {
            postAdapter.posts[index].isLiking = false
            if (postAdapter.posts[index].isLiked) postAdapter.posts[index].likedBy.remove(
                FirebaseAuth.getInstance().currentUser?.uid
            ) else postAdapter.posts[index].likedBy.add(
                FirebaseAuth.getInstance().currentUser!!.uid
            )
            postAdapter.posts[index].isLiked = !postAdapter.posts[index].isLiked
            postAdapter.notifyItemChanged(index)
            binding.swipeRefreshLayout.isRefreshing = false
        })
    }

    private fun animateFAB() {
        if (isFabOpen) {
            binding.apply {
                createPostFb.startAnimation(rotateBackward)
                fabCreateImagePost.startAnimation(fabClose)
                fabCreateTextPost.startAnimation(fabClose)
                fabCreateImagePost.isClickable = false
                fabCreateTextPost.isClickable = false
            }
            isFabOpen = false
        } else {
            binding.apply {
                createPostFb.startAnimation(rotateForward)
                fabCreateImagePost.startAnimation(fabOpen)
                fabCreateTextPost.startAnimation(fabOpen)
                fabCreateImagePost.isClickable = true
                fabCreateTextPost.isClickable = true
            }
            isFabOpen = true
        }
    }

    private fun showConfirmationDialog(post: Post) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Post")
            .setMessage("Are you sure you want to delete this post?")
            .setPositiveButton(
                "Yes"
            ) { _, _ ->
                viewModel.deletePost(post)
            }
            .setNegativeButton("No", null)
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show()
    }

    private fun setUpRecyclerView() {

        postAdapter.setOnCommentClickListener { post ->
            findNavController().navigate(
                R.id.action_homeFragment_to_commentFragment,
                Bundle().apply {
                    putString("postId", post.id)
                }
            )
        }

        postAdapter.setOnDeleteClickListener { post ->
            showConfirmationDialog(post)
        }

        postAdapter.setOnLikeClickListener { post, i ->
            post.isLiking = true
            index = i
            viewModel.likePost(post)
        }

        postAdapter.setNavigateToProfileListener { uid, userName ->
            if (FirebaseAuth.getInstance().currentUser?.uid == uid) {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToUserProfileFragment()
                )
            } else {
                findNavController().navigate(
                    HomeFragmentDirections.actionHomeFragmentToSearchedProfileFragment(
                        uid = uid,
                        username = userName
                    )
                )
            }
        }

        postAdapter.setOnLikedByClickListener { uid ->
            findNavController().navigate(
                HomeFragmentDirections.actionHomeFragmentToLikedByFragment(uid)
            )
        }

        binding.postRv.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = postAdapter
        }
    }
}