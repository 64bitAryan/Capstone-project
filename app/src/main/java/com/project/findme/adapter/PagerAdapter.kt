package com.project.findme.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.project.findme.utils.Constants

class PagerAdapter(fm: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fm, lifecycle) {

    private val fragmentList = arrayListOf<Fragment>()

    fun addFragment(fragment: Fragment, uid: String, from: String) {
        fragmentList.add(fragment)
        fragment.arguments = Bundle().apply {
            putStringArrayList(Constants.FRAGMENT_ARG_KEY, arrayListOf(uid, from))
        }
    }

    override fun getItemCount(): Int {
        return fragmentList.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}