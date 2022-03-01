package com.amagen.supercheap.onboarding

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter

class ViewPagerAdapter(
    private val fragmentList:List<Fragment>,
    fm:FragmentManager,
    lifecycle:Lifecycle
):FragmentStateAdapter(fm,lifecycle) {
    override fun getItemCount()=fragmentList.size

    override fun createFragment(position: Int): Fragment {
        return fragmentList[position]
    }
}