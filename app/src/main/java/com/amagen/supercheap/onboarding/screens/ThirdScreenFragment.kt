package com.amagen.supercheap.onboarding.screens

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.amagen.supercheap.MainActivityApplication
import com.amagen.supercheap.R
import com.amagen.supercheap.databinding.FragmentSecondScreenBinding
import com.amagen.supercheap.databinding.FragmentThirdScreenBinding

class ThirdScreenFragment : Fragment() {

    private var _binding: FragmentThirdScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentThirdScreenBinding.inflate(layoutInflater)



        binding.tvNextTo2.setOnClickListener {
            findNavController().navigate(R.id.action_viewPagerFragment_to_dashboardFragment)
            val sharedPref = requireActivity().getSharedPreferences("onboarding", Context.MODE_PRIVATE)
            sharedPref.edit().putBoolean("finish",true).apply()
            (activity as MainActivityApplication).showNavBar()

        }


        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
