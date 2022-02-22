package com.amagen.supercheap.ui.home

import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.amagen.supercheap.R
import com.amagen.supercheap.databinding.FragmentHomeBinding
import com.amagen.supercheap.extensions.delayOnLifeCycle
import com.amagen.supercheap.network.NetworkStatusChecker
import com.amagen.supercheap.ui.home.searchproducts.findTheCheapestSuper.FindTheChpeastSuperFragment

open class HomeFragment : Fragment() {


    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        //checkConnectivityStatus()



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