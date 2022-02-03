package com.amagen.supercheap.ui.home

import android.app.Dialog
import android.content.Intent
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.amagen.supercheap.R
import com.amagen.supercheap.auth.AuthActivity
import com.amagen.supercheap.databinding.FragmentHomeBinding
import com.amagen.supercheap.extensions.setDialogIfApplicationLoadingData
import com.amagen.supercheap.network.NetworkStatusChecker
import com.amagen.supercheap.ui.home.searchproducts.bylist.ListSearchProducts
import com.google.firebase.auth.FirebaseAuth
import java.lang.IndexOutOfBoundsException

open class HomeFragment : Fragment() {

    private lateinit var homeViewModel: HomeViewModel
    private var _binding: FragmentHomeBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        homeViewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignOut.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            activity?.startActivity(Intent(activity, AuthActivity::class.java))
            activity?.finish()
        }
        checkConnectivityStatus()



        binding.btnMoveToFindProductFragment.setOnClickListener {
            findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToSingleSearchProduct())
        }
        binding.btnAddListOfProducts.setOnClickListener {
//            findNavController().navigate(HomeFragmentDirections.actionNavigationHomeToListSearchProducts())
            parentFragmentManager
                .beginTransaction()
                .replace(
                    R.id.nav_host_fragment_activity_main_application
                    , ListSearchProducts()
                )
                .addToBackStack(null)
                .commit()
        }

    }

    private fun checkConnectivityStatus() {
        //check connectivity status
        val connectivityManager = requireActivity().getSystemService(ConnectivityManager::class.java)
        if(NetworkStatusChecker(connectivityManager).hasInternetConnection()){
            println("connected to the internet")
        }else{
            Toast.makeText(context, "you don't have Intent connection",Toast.LENGTH_SHORT).show()
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}