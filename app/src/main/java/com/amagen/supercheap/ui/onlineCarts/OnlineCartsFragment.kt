package com.amagen.supercheap.ui.onlineCarts

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.amagen.supercheap.R
import com.amagen.supercheap.databinding.FragmentHistoryBinding
import com.amagen.supercheap.extensions.checkConnectivityStatus
import com.amagen.supercheap.extensions.hideCorners
import com.amagen.supercheap.models.Cart
import com.amagen.supercheap.recycleview.ShoppingCartRecycleView
import com.amagen.supercheap.ui.FunctionalFragment
import com.amagen.supercheap.ui.home.searchproducts.singleSuperSearch.SuperSearchFragment
import kotlinx.android.synthetic.main.search_for_cart_dialog.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class OnlineCartsFragment : FunctionalFragment(), ShoppingCartRecycleView.OnCartClickedListener  {

    private lateinit var onlineCartsViewModel: OnlineCartsViewModel
    private var _binding: FragmentHistoryBinding? = null
    private lateinit var searchDialog:Dialog
    private lateinit var pendingJob:Job
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        onlineCartsViewModel = ViewModelProvider(this)[OnlineCartsViewModel::class.java]
        _binding = FragmentHistoryBinding.inflate(inflater, container, false)
        searchDialog = Dialog(requireContext())
        setMainActivityViewModel(requireActivity())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if(onlineCartsViewModel.carts.value.isNullOrEmpty()){
            if(requireActivity().checkConnectivityStatus(mainActivityViewModel, lifecycleScope = lifecycleScope)){
                pendingJob=lifecycleScope.launch {
                    binding.pbLoading.visibility=View.VISIBLE
                    delay(1000)
                    binding.pbLoading.visibility=View.GONE
                    binding.tvNoOnlineCarts.visibility=View.VISIBLE
                }
            }
        }
        onlineCartsViewModel.carts.observe(viewLifecycleOwner){
            binding.rvCarts.adapter = ShoppingCartRecycleView(it,this)
            binding.rvCarts.layoutManager = LinearLayoutManager(requireContext())
            if(it.isNotEmpty()){
                if(this::pendingJob.isInitialized){
                    pendingJob.cancel()
                }
                binding.tvNoOnlineCarts.visibility = View.GONE
                binding.rvCarts.visibility=View.VISIBLE
                binding.pbLoading.visibility=View.GONE

            }
        }

        binding.fabSearchCart.setOnClickListener {
            onlineCartsViewModel.getAllUploaderByName()

            onlineCartsViewModel.uploaderNames.observe(viewLifecycleOwner){
                val adapter = ArrayAdapter(
                    requireContext(), android.R.layout.simple_expandable_list_item_1, it.map { it }.toSet().toList())
                searchDialog.setContentView(R.layout.search_for_cart_dialog)
                searchDialog.search_uploader.setOnItemClickListener { parent, view, position, id ->
                    onlineCartsViewModel.getCartByUploader(parent.getItemAtPosition(position).toString())

                    onlineCartsViewModel.uploaderCart.observe(viewLifecycleOwner){
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
            onlineCartsViewModel.attachCartToProfile(cart)
            searchDialog.dismiss()
        }


        val fragment = SuperSearchFragment(
            cart.items,
            cart.brandandstoreToprice.storeId_To_BrandId
        )

        parentFragmentManager.beginTransaction().replace(
            R.id.nav_host_fragment_activity_main_application, fragment
        ).addToBackStack(null).commit()
    }
}