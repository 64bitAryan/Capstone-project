package com.project.findme.mainactivity.mainfragments.ui.listFollowers

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.project.findme.adapter.PagerAdapter
import com.project.findme.mainactivity.mainfragments.ui.followersList.FollowersListFragment
import com.project.findme.mainactivity.mainfragments.ui.followingsList.FollowingListFragment
import com.project.findme.mainactivity.mainfragments.ui.mutualsList.MutualsListFragment
import com.project.findme.mainactivity.mainfragments.ui.suggestionList.SuggestionListFragment
import com.ryan.findme.R
import com.ryan.findme.databinding.FragmentListsFollowersBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ListFollowersFragment : Fragment(R.layout.fragment_lists_followers) {

    private lateinit var binding: FragmentListsFollowersBinding
    private val args: ListFollowersFragmentArgs by navArgs()
    private lateinit var pagerAdapter: PagerAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentListsFollowersBinding.bind(view)

        binding.apply {
            tabLayoutList.setupWithViewPager(viewPager)
            pagerAdapter = PagerAdapter(childFragmentManager)
            pagerAdapter.addFragment(MutualsListFragment(), "Mutuals", args.uid)
            pagerAdapter.addFragment(FollowersListFragment(), "Followers", args.uid)
            pagerAdapter.addFragment(FollowingListFragment(), "Followings", args.uid)
            pagerAdapter.addFragment(SuggestionListFragment(), "Suggestions", args.uid)
            viewPager.adapter = pagerAdapter

            viewPager.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabLayoutList))
            tabLayoutList.addOnTabSelectedListener(object : OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    viewPager.currentItem = tab.position
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {}
                override fun onTabReselected(tab: TabLayout.Tab) {}
            })
            viewPager.offscreenPageLimit = 4
            when (args.type) {
                "Followers" -> {
                    tabLayoutList.selectTab(tabLayoutList.getTabAt(1))
                }
                "Followings" -> {
                    tabLayoutList.selectTab(tabLayoutList.getTabAt(2))
                }
            }
        }
    }
}
