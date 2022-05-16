package com.project.findme.mainactivity.mainfragments.ui.listFollowersUser

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.project.findme.adapter.PagerAdapter
import com.project.findme.mainactivity.mainfragments.ui.followersList.FollowersListFragment
import com.project.findme.mainactivity.mainfragments.ui.followingsList.FollowingListFragment
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentListsFollowersUserBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ListFollowersFragmentUser : Fragment(R.layout.fragment_lists_followers_user) {

    private lateinit var binding: FragmentListsFollowersUserBinding
    private val args: ListFollowersFragmentUserArgs by navArgs()
    private lateinit var pagerAdapter: PagerAdapter
    private val tabTitles = listOf("Followers", "Followings")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentListsFollowersUserBinding.bind(view)

        binding.apply {
            pagerAdapter = PagerAdapter(
                childFragmentManager,
                viewPagerUser.findViewTreeLifecycleOwner()?.lifecycle!!
            )
            val uid = FirebaseAuth.getInstance().currentUser!!.uid
            pagerAdapter.addFragment(
                FollowersListFragment(),
                uid,
                "ListFollowersUser"
            )
            pagerAdapter.addFragment(
                FollowingListFragment(),
                uid,
                "ListFollowersUser"
            )
            viewPagerUser.adapter = pagerAdapter

            TabLayoutMediator(tabLayoutListUser, viewPagerUser) { tab, position ->
                tab.text = tabTitles[position]
            }.attach()

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
                    viewPagerUser.setCurrentItem(0, false)
                }
                "Followings" -> {
                    tabLayoutListUser.selectTab(tabLayoutListUser.getTabAt(1))
                    viewPagerUser.setCurrentItem(1, false)
                }
            }
        }
    }
}