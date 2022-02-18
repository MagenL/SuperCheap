package com.amagen.supercheap.ui.history.selectedProducts

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.amagen.supercheap.R
import com.amagen.supercheap.databinding.SelectedItemsFragmentBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference

class SelectedItems : Fragment() {


    var _binding:SelectedItemsFragmentBinding?=null
    val binding get() = _binding

    private lateinit var viewModel: SelectedItemsViewModel

    private lateinit var mAuth:FirebaseAuth
    private lateinit var productAtSuperReference:DatabaseReference


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewModel = ViewModelProvider(this).get(SelectedItemsViewModel::class.java)
        _binding = SelectedItemsFragmentBinding.inflate(layoutInflater,container,false)
        return binding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }


}