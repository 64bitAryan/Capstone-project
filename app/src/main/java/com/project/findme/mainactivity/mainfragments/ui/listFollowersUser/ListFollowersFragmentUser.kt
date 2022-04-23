package com.project.findme.mainactivity.mainfragments.ui.listFollowersUser

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.tabs.TabLayout
import com.google.firebase.auth.FirebaseAuth
import com.project.findme.adapter.PagerAdapter
import com.project.findme.mainactivity.mainfragments.ui.listFollowers.FollowersListFragment
import com.project.findme.mainactivity.mainfragments.ui.listFollowers.FollowingListFragment
import com.project.findme.mainactivity.mainfragments.ui.listFollowers.MutualsListFragment
import com.project.findme.utils.EventObserver
import com.project.findme.utils.snackbar
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentListsFollowersUserBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ListFollowersFragmentUser : Fragment(R.layout.fragment_lists_followers_user) {

    private lateinit var binding: FragmentListsFollowersUserBinding
    private val args: ListFollowersFragmentUserArgs by navArgs()
    private lateinit var pagerAdapter: PagerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentListsFollowersUserBinding.bind(view)

        binding.apply {
            tabLayoutListUser.setupWithViewPager(viewPagerUser)
            pagerAdapter = PagerAdapter(childFragmentManager)
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            pagerAdapter.addFragment(FollowersListFragment(), "Followers", uid)
            pagerAdapter.addFragment(FollowingListFragment(), "Followings", uid)
            viewPagerUser.adapter = pagerAdapter
            viewPagerUser.addOnPageChangeListener(
                TabLayout.TabLayoutOnPageChangeListener(
                    tabLayoutListUser
                )
            )
            tabLayoutListUser.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    viewPagerUser.currentItem = tab.position
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
            viewPagerUser.offscreenPageLimit = 2
            when (args.type) {
                "Followers" -> {
                    tabLayoutListUser.selectTab(tabLayoutListUser.getTabAt(0))
                }
                "Followings" -> {
                    tabLayoutListUser.selectTab(tabLayoutListUser.getTabAt(1))
                }
            }
        }
    }
}