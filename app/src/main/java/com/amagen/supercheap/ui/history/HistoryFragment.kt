package com.amagen.supercheap.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.amagen.supercheap.databinding.FragmentHistoryBinding
import com.amagen.supercheap.models.Cart
import com.amagen.supercheap.recycleview.RecycleviewShoppingCart

class HistoryFragment : Fragment(), RecycleviewShoppingCart.OnCartClickedListener  {

    private lateinit var dashboardViewModel: HistoryViewModel
    private var _binding: FragmentHistoryBinding? = null


    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        dashboardViewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dashboardViewModel.userCart.observe(viewLifecycleOwner){
            binding.rvCarts.adapter= RecycleviewShoppingCart(it,this)
            binding.rvCarts.layoutManager= LinearLayoutManager(requireContext())
        }




    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun CartClicked(cart: Cart) {
        TODO("Not yet implemented")
    }
}