package com.amagen.supercheap.onboarding

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import com.amagen.supercheap.R
import com.amagen.supercheap.databinding.FragmentViewPagerBinding
import com.amagen.supercheap.onboarding.screens.FirstScreenFragment
import com.amagen.supercheap.onboarding.screens.SecondScreenFragment
import com.amagen.supercheap.onboarding.screens.ThirdScreenFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class ViewPagerFragment : Fragment() {

    private var _binding:FragmentViewPagerBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewPagerBinding.inflate(layoutInflater)




        val fragments = arrayListOf(
            FirstScreenFragment(),
            SecondScreenFragment(),
            ThirdScreenFragment()
        )

        binding.vpFragment.adapter= ViewPagerAdapter(
            fragments,
            requireActivity().supportFragmentManager,
            lifecycle
        )
        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        //
        val callback = object : OnBackPressedCallback(true){override fun handleOnBackPressed(){}}
        requireActivity().onBackPressedDispatcher.addCallback(this,callback)
    }

}