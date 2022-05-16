package com.project.findme.mainactivity.mainfragments.ui.listFollowers

import android.os.Bundle
import android.os.Handler
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.findViewTreeLifecycleOwner
import androidx.navigation.fragment.navArgs
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import com.google.android.material.tabs.TabLayoutMediator
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
    private val tabTitles = listOf("Mutuals", "Followers", "Followings", "Suggestions")

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentListsFollowersBinding.bind(view)

        binding.apply {

            pagerAdapter =
                PagerAdapter(
                    childFragmentManager,
                    viewPager.findViewTreeLifecycleOwner()?.lifecycle!!
                )

            pagerAdapter.addFragment(
                MutualsListFragment(),
                args.uid,
                "ListFollowers"
            )

            pagerAdapter.addFragment(
                FollowersListFragment(),
                args.uid,
                "ListFollowers"
            )

            pagerAdapter.addFragment(
                FollowingListFragment(),
                args.uid,
                "ListFollowers"
            )

            pagerAdapter.addFragment(
                SuggestionListFragment(),
                args.uid,
                "ListFollowers"
            )

            viewPager.adapter = pagerAdapter

            TabLayoutMediator(tabLayoutList, viewPager) { tab, position ->
                tab.text = tabTitles[position]
            }.attach()

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
                    viewPager.setCurrentItem(1, false)
                }
                "Followings" -> {
                    tabLayoutList.selectTab(tabLayoutList.getTabAt(2))
                    viewPager.setCurrentItem(2, false)
                }
            }
        }
    }
}
