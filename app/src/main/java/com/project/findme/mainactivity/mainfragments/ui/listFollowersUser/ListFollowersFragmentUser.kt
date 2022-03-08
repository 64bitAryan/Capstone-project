package com.project.findme.mainactivity.mainfragments.ui.listFollowersUser

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.Window
import android.widget.ImageView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.OnItemTouchListener
import com.bumptech.glide.RequestManager
import com.google.firebase.auth.FirebaseAuth
import com.project.findme.adapter.ListAdapter
import com.project.findme.data.entity.Post
import com.project.findme.utils.EventObserver
import com.project.findme.utils.snackbar
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentListsFollowersUserBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ListFollowersFragmentUser : Fragment(R.layout.fragment_lists_followers_user) {

    @Inject
    lateinit var listAdapter: ListAdapter

    @Inject
    lateinit var glide: RequestManager
    private lateinit var viewModel: ListFollowersUserViewModel
    private lateinit var binding: FragmentListsFollowersUserBinding
    private val args: ListFollowersFragmentUserArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel = ViewModelProvider(requireActivity())[ListFollowersUserViewModel::class.java]
        subscribeToObserve()

        binding = FragmentListsFollowersUserBinding.bind(view)

        FirebaseAuth.getInstance().currentUser?.let { viewModel.getUsers(it.uid, args.type) }

        when (args.type) {
            "Followers" -> {
                binding.btnFollowersList.setBackgroundResource(R.drawable.button_bg)
                binding.btnFollowingsList.setBackgroundResource(0)
            }
            "Followings" -> {
                binding.btnFollowingsList.setBackgroundResource(R.drawable.button_bg)
                binding.btnFollowersList.setBackgroundResource(0)
            }
        }

        setUpRecyclerView()

        binding.apply {
            btnFollowersList.setOnClickListener {
                FirebaseAuth.getInstance().currentUser?.let { user ->
                    viewModel.getUsers(
                        user.uid,
                        "Followers"
                    )
                }
                it.setBackgroundResource(R.drawable.button_bg)
                btnFollowingsList.setBackgroundResource(0)
            }
            btnFollowingsList.setOnClickListener {
                FirebaseAuth.getInstance().currentUser?.let { user ->
                    viewModel.getUsers(
                        user.uid,
                        "Followings"
                    )
                }
                it.setBackgroundResource(R.drawable.button_bg)
                btnFollowersList.setBackgroundResource(0)
            }
        }
    }

    private fun setUpRecyclerView() {
        binding.rvListFollowers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = listAdapter
        }
    }

    private fun subscribeToObserve() {
        viewModel.list.observe(viewLifecycleOwner, EventObserver(
            onError = {
                binding.progressBarLists.isVisible = false
                snackbar(it)
            },
            onLoading = {
                listAdapter.users = listOf()
                binding.progressBarLists.isVisible = true
            }
        ) { users ->
            listAdapter.users = users
            binding.progressBarLists.isVisible = false
        })
    }
}