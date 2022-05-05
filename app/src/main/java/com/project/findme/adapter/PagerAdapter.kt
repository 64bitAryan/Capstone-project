package com.project.findme.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import com.project.findme.utils.Constants

class PagerAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    private val fragmentList = arrayListOf<Fragment>()
    private val titles = arrayListOf<String>()

    override fun getCount(): Int = fragmentList.size

    override fun getItem(i: Int): Fragment {
        return fragmentList[i]
    }

    override fun getPageTitle(position: Int): CharSequence {
        return titles[position]
    }

    fun addFragment(fragment: Fragment, title: String, uid: String, from: String) {
        fragmentList.add(fragment)
        titles.add(title)
        fragment.arguments = Bundle().apply {
            putStringArrayList(Constants.FRAGMENT_ARG_KEY, arrayListOf(uid, from))
        }
    }
}