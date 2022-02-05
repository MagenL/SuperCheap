package com.amagen.supercheap.ui.home.searchproducts.bylist


import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.FragmentManager

import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.amagen.supercheap.MainActivityViewModel
import com.amagen.supercheap.R
import com.amagen.supercheap.databinding.ListSearchProductsFragmentBinding
import com.amagen.supercheap.models.BrandAndStore_toPrice
import com.amagen.supercheap.models.BrandToId
import com.amagen.supercheap.models.Item
import com.amagen.supercheap.models.StoreId_To_BrandId
import com.amagen.supercheap.ui.FunctionalFragment
import com.amagen.supercheap.recycleview.SingleProductRecycleView
import com.amagen.supercheap.recycleview.SuperAtBrandWithTotalPriceRecycleView
import com.amagen.supercheap.ui.home.searchproducts.bysingle.SingleSearchProduct
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class ListSearchProducts : FunctionalFragment(), SingleProductRecycleView.OnItemClickListener ,
    SuperAtBrandWithTotalPriceRecycleView.OnSuperClickListener{




    private lateinit var viewModel: ListSearchProductsViewModel
    private lateinit var mainActivityViewModel:MainActivityViewModel

    private var _binding:ListSearchProductsFragmentBinding?=null
    val binding get() = _binding!!

    private val sumOfSuperAndTotalPrice= ArrayList<StoreId_To_BrandId>()
    val items = ArrayList<Item>()
    var myParentFragmentManger: FragmentManager?= null

    var conditionSearch:Boolean = false

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
        observeItemsForAllSupers()

        initUIForSearchButtons()





        binding.btnSearchAllSupers.setOnClickListener {
            it.isSelected=it.isSelected.not()
            var num:Int=0
            if(it.isSelected) {

                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.clear()
                    println("num=$num")
                    viewModel.getDuplicateItemsFromAllSupers(mainActivityViewModel.db,
                        condition =mainActivityViewModel.db.FullItemTableDao().getUserStoresCount()-1
                    )
                }.invokeOnCompletion {
                    lifecycleScope.launch(Dispatchers.Main) {
                        observeItemsForAllSupers()
                    }
                }

                observeItemsForAllSupers()
                binding.btnSearchVictory.isEnabled=false
                binding.btnSearchVictory.isClickable=false

                binding.btnSearchMahsaniashok.isEnabled=false
                binding.btnSearchMahsaniashok.isClickable=false

                binding.btnSearchBareket.isEnabled=false
                binding.btnSearchBareket.isClickable=false

                binding.btnSearchHCohen.isEnabled=false
                binding.btnSearchHCohen.isClickable=false

                binding.btnSearchShufersal.isEnabled=false
                binding.btnSearchShufersal.isClickable=false
            } else{
                viewModel.clear()
                viewModel.getDuplicateItemsFromAllSupers(mainActivityViewModel.db)
                observeItemsForAllSupers()
                viewModel.clearTerms()
                binding.btnSearchVictory.isEnabled=true
                binding.btnSearchVictory.isClickable=true

                binding.btnSearchMahsaniashok.isEnabled=true
                binding.btnSearchMahsaniashok.isClickable=true

                binding.btnSearchBareket.isEnabled=true
                binding.btnSearchBareket.isClickable=true

                binding.btnSearchHCohen.isEnabled=true
                binding.btnSearchHCohen.isClickable=true

                binding.btnSearchShufersal.isEnabled=true
                binding.btnSearchShufersal.isClickable=true
            }
            println(viewModel.showTerms())
        }


        binding.btnSearchShufersal.setOnClickListener {
            it.isSelected=it.isSelected.not()
            viewModel.shufersal=BrandToId.SHUFERSAL.brandId
            viewModel.getDuplicateItemsFromAllSupers(mainActivityViewModel.db,1)
        }
        binding.btnSearchVictory.setOnClickListener {
            it.isSelected=it.isSelected.not()
            viewModel.victory=BrandToId.VICTORY.brandId
            viewModel.getDuplicateItemsFromAllSupers(mainActivityViewModel.db,1)
        }
        binding.btnSearchHCohen.setOnClickListener {
            it.isSelected=it.isSelected.not()
            viewModel.hcohen=BrandToId.HCohen.brandId
            viewModel.getDuplicateItemsFromAllSupers(mainActivityViewModel.db,1)
        }
        binding.btnSearchBareket.setOnClickListener {
            it.isSelected=it.isSelected.not()
            viewModel.bareket=BrandToId.SuperBareket.brandId
            viewModel.getDuplicateItemsFromAllSupers(mainActivityViewModel.db,1)
        }
        binding.btnSearchMahsaniashok.setOnClickListener {
            it.isSelected=it.isSelected.not()
            viewModel.mahsaniAshok=BrandToId.MahsaniAshok.brandId
            viewModel.getDuplicateItemsFromAllSupers(mainActivityViewModel.db,1)
        }


        binding.rvChosenItems.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)

        binding.rvChosenItems.adapter = SingleProductRecycleView(items,this)









        binding.btnSearchForMatchesInAllUserSupers.setOnClickListener {
            if(binding.rvSupersResult.adapter != null){
                viewModel.clear()
                binding.rvSupersResult.adapter!!.notifyDataSetChanged()
            }
            if(items.isNotEmpty()){
                conditionSearch = binding.btnSearchBareket.isSelected||binding.btnSearchHCohen.isSelected||binding.btnSearchShufersal.isSelected||binding.btnSearchVictory.isSelected||binding.btnSearchMahsaniashok.isSelected
                viewModel.getAvailableSupersFromItemList(items,mainActivityViewModel.db,conditionSearch)
            }
            viewModel.superAtBrand.observe(viewLifecycleOwner){
                binding.rvSupersResult.adapter = SuperAtBrandWithTotalPriceRecycleView(viewModel.brandAndStoreStore_ToPrice,ListSearchProducts(),myParentFragmentManger,items)
                binding.rvSupersResult.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
            }

        }
    }

    private fun observeItemsForAllSupers() {
        viewModel.itemFromAllSupers.observe(viewLifecycleOwner) {
            Log.d("itemsFromAllSUPERS", "observed, empty?${it.isEmpty()}")
            getObserverForSelectedItems(it)
        }
    }

    private fun getObserverForSelectedItems(it: List<Item>) {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_expandable_list_item_1,
            it.map { it.itemName })
        binding.searchProductFromAllDb.setAdapter(adapter)
        binding.searchProductFromAllDb.threshold = 1
        binding.searchProductFromAllDb.setOnItemClickListener { parent, view, position, id ->
            val selectedItem = it.find { it.itemName == parent.getItemAtPosition(position) }
            items.add(selectedItem!!)
            binding.rvChosenItems.adapter?.notifyItemInserted(items.size - 1)
            binding.searchProductFromAllDb.setText("")
        }
    }


    private fun initUIForSearchButtons() {
        viewModel.getAllBrands(mainActivityViewModel.db)
        viewModel.userBrands.observe(viewLifecycleOwner) {
            it.forEach {
                println("brand = $it")
                when (it) {
                    BrandToId.SHUFERSAL.brandId -> {
                        binding.btnSearchShufersal.visibility = View.VISIBLE
                    }
                    BrandToId.HCohen.brandId -> {
                        binding.btnSearchHCohen.visibility = View.VISIBLE
                    }
                    BrandToId.SuperBareket.brandId -> {
                        binding.btnSearchBareket.visibility = View.VISIBLE
                    }
                    BrandToId.MahsaniAshok.brandId -> {
                        binding.btnSearchMahsaniashok.visibility = View.VISIBLE
                    }
                    BrandToId.VICTORY.brandId -> {
                        binding.btnSearchVictory.visibility = View.VISIBLE
                    }
                }
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