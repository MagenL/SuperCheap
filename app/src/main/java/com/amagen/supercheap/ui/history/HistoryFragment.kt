package com.amagen.supercheap.ui.history

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.amagen.supercheap.R
import com.amagen.supercheap.databinding.FragmentHistoryBinding
import com.amagen.supercheap.extensions.hideCorners
import com.amagen.supercheap.models.Cart
import com.amagen.supercheap.recycleview.ShoppingCartRecycleView
import com.amagen.supercheap.ui.home.searchproducts.bysingle.SingleSearchProduct
import kotlinx.android.synthetic.main.search_for_cart_dialog.*

class HistoryFragment : Fragment(), ShoppingCartRecycleView.OnCartClickedListener  {

    private lateinit var historyViewModel: HistoryViewModel
    private var _binding: FragmentHistoryBinding? = null
    private lateinit var searchDialog:Dialog

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        historyViewModel = ViewModelProvider(this).get(HistoryViewModel::class.java)
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        searchDialog = Dialog(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        historyViewModel.carts.observe(viewLifecycleOwner){
            binding.rvCarts.adapter = ShoppingCartRecycleView(it,this)
            binding.rvCarts.layoutManager = LinearLayoutManager(requireContext())
        }

        binding.fabSearchCart.setOnClickListener {
            historyViewModel.getAllUploaderByName()

            historyViewModel.uploaderNames.observe(viewLifecycleOwner){
                val adapter = ArrayAdapter(
                    requireContext(), android.R.layout.simple_expandable_list_item_1, it.map { it }.toSet().toList())
                searchDialog.setContentView(R.layout.search_for_cart_dialog)
                searchDialog.search_uploader.setOnItemClickListener { parent, view, position, id ->
                    historyViewModel.getCartByUploader(parent.getItemAtPosition(position).toString())

                    historyViewModel.uploaderCart.observe(viewLifecycleOwner){
                        searchDialog.rv_carts_of_uploader.layoutManager=LinearLayoutManager(requireContext())
                        searchDialog.rv_carts_of_uploader.adapter = ShoppingCartRecycleView(it,this)
                    }
                }
                searchDialog.hideCorners()

                searchDialog.search_uploader.setAdapter(adapter)
                searchDialog.show()
            }




        }



    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun CartClicked(cart: Cart) {
        if(searchDialog.isShowing){
            historyViewModel.attachCartToProfile(cart)
            searchDialog.dismiss()
        }


        val fragment = SingleSearchProduct(
            cart.items,
            cart.brandandstoreToprice.storeId_To_BrandId
        )

        parentFragmentManager.beginTransaction().replace(
            R.id.nav_host_fragment_activity_main_application, fragment
        ).addToBackStack(null).commit()
    }
}