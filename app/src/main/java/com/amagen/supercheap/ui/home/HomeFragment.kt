package com.amagen.supercheap.ui.home

import android.content.Context
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.compose.ui.text.toLowerCase
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.view.marginBottom
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.amagen.supercheap.R
import com.amagen.supercheap.databinding.FragmentHomeBinding
import com.amagen.supercheap.extensions.delayOnLifeCycle
import com.amagen.supercheap.network.NetworkStatusChecker
import com.amagen.supercheap.ui.home.searchproducts.findTheCheapestSuper.FindTheChpeastSuperFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.util.*

open class HomeFragment : Fragment() {


    private var _binding: FragmentHomeBinding? = null

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        if(Locale.getDefault().language.lowercase()=="he"||Locale.getDefault().language.lowercase()=="iw"){
            binding.tvMagnifier.x= -200.0F
            binding.tvMagnifier.text = "חפש את הסופר\n הזול ביותר"
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)







        binding.btnMoveToFindProductFragment.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToSingleSearchProduct())
        }
        binding.btnAddListOfProducts.setOnClickListener {
//            findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToListSearchProducts())
            parentFragmentManager
                .beginTransaction()
                .replace(
                    R.id.nav_host_fragment_activity_main_application
                    , FindTheChpeastSuperFragment()
                )
                .addToBackStack(null)
                .commit()
        }

    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}