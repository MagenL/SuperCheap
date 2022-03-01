package com.amagen.supercheap.onboarding.screens

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.amagen.supercheap.R
import com.amagen.supercheap.databinding.FragmentFirstScreenBinding

class FirstScreenFragment : Fragment() {
    private var _binding:FragmentFirstScreenBinding?=null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFirstScreenBinding.inflate(layoutInflater)



        val viewPager = requireActivity().findViewById<ViewPager2>(R.id.vp_fragment)

        binding.tvNextTo2.setOnClickListener {
            viewPager?.currentItem = 1
        }

        return binding.root
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }

}