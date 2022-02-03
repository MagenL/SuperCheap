package com.amagen.supercheap.ui.home.searchproducts.bylist

import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.amagen.supercheap.MainActivityViewModel
import com.amagen.supercheap.R
import com.amagen.supercheap.databinding.ListSearchProductsFragmentBinding
import com.amagen.supercheap.models.BrandAndStore_toPrice
import com.amagen.supercheap.models.Item
import com.amagen.supercheap.models.StoreId_To_BrandId
import com.amagen.supercheap.ui.FunctionalFragment
import com.amagen.supercheap.recycleview.SingleProductRecycleView
import com.amagen.supercheap.recycleview.SuperAtBrandWithTotalPriceRecycleView
import com.amagen.supercheap.ui.home.searchproducts.bysingle.SingleSearchProduct

class ListSearchProducts : FunctionalFragment(), SingleProductRecycleView.OnItemClickListener ,
    SuperAtBrandWithTotalPriceRecycleView.OnSuperClickListener{




    private lateinit var viewModel: ListSearchProductsViewModel
    private lateinit var mainActivityViewModel:MainActivityViewModel

    private var _binding:ListSearchProductsFragmentBinding?=null
    val binding get() = _binding!!

    private val sumOfSuperAndTotalPrice= ArrayList<StoreId_To_BrandId>()
    val items = ArrayList<Item>()
    var myParentFragmentManger:FragmentManager?= null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ListSearchProductsFragmentBinding.inflate(layoutInflater,container,false)
        viewModel = ViewModelProvider(this).get(ListSearchProductsViewModel::class.java)
        mainActivityViewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
        myParentFragmentManger = parentFragmentManager

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getDuplicateItemsFromAllSupers(mainActivityViewModel.db)


        binding.rvChosenItems.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)

        binding.rvChosenItems.adapter = SingleProductRecycleView(items,this)




        viewModel.itemFromAllSupers.observe(viewLifecycleOwner){
            Log.d("itemsFromAllSUPERS", "observed, empty?${it.isEmpty()}" )
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_expandable_list_item_1,it.map { it.itemName })
            binding.searchProductFromAllDb.setAdapter(adapter)
            binding.searchProductFromAllDb.threshold=1
            binding.searchProductFromAllDb.setOnItemClickListener { parent, view, position, id ->
                val selectedItem = it.find { it.itemName==parent.getItemAtPosition(position) }
                items.add(selectedItem!!)
                binding.rvChosenItems.adapter?.notifyItemInserted(items.size-1)
                binding.searchProductFromAllDb.setText("")
            }
        }




        binding.btnSearchForMatchesInAllUserSupers.setOnClickListener {
            if(binding.rvSupersResult.adapter != null){
                viewModel.clear()
                binding.rvSupersResult.adapter!!.notifyDataSetChanged()

            }
            if(items.isNotEmpty()){
                viewModel.getAvailableSupersFromItemList(items,mainActivityViewModel.db)
            }
            viewModel.superAtBrand.observe(viewLifecycleOwner){
                binding.rvSupersResult.adapter = SuperAtBrandWithTotalPriceRecycleView(viewModel.brandAndStoreStore_ToPrice,ListSearchProducts(),myParentFragmentManger,items)
                binding.rvSupersResult.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            }

        }
    }

    override fun onItemRootClick(item: Item, adapterPosition: Int) {
        dialogToItem(adapterPosition,item,null,binding,items)
    }

    override fun onItemRootLongClick(item: Item, position: Int) {

    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }




    


    override fun onSuperRootClick(
        superWithPrice: BrandAndStore_toPrice,
        myParentFragmentManger: FragmentManager?,
        items: List<Item>
    ) {

        val fragment = SingleSearchProduct(
            items,
            StoreId_To_BrandId(superWithPrice.storeId_To_BrandId.storeId,superWithPrice.storeId_To_BrandId.brandId)
        )


        myParentFragmentManger!!.beginTransaction().replace(
            R.id.nav_host_fragment_activity_main_application, fragment
        ).addToBackStack(null).commit()

    }
}