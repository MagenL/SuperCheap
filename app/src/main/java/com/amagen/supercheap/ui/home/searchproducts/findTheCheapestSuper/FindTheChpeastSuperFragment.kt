package com.amagen.supercheap.ui.home.searchproducts.findTheCheapestSuper


import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.lifecycleScope

import androidx.recyclerview.widget.LinearLayoutManager
import com.amagen.supercheap.R
import com.amagen.supercheap.databinding.ListSearchProductsFragmentBinding
import com.amagen.supercheap.extensions.delayOnLifeCycle
import com.amagen.supercheap.models.BrandAndStore_toPrice
import com.amagen.supercheap.models.BrandToId
import com.amagen.supercheap.models.Item
import com.amagen.supercheap.models.StoreId_To_BrandId
import com.amagen.supercheap.ui.FunctionalFragment
import com.amagen.supercheap.recycleview.SingleProductRecycleView
import com.amagen.supercheap.recycleview.SuperAtBrandWithTotalPriceRecycleView
import com.amagen.supercheap.ui.home.searchproducts.singleSuperSearch.SuperSearchFragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

class FindTheChpeastSuperFragment : FunctionalFragment(), SingleProductRecycleView.OnItemClickListener ,
    SuperAtBrandWithTotalPriceRecycleView.OnSuperClickListener{




    private lateinit var viewModel: FindTheCheapestSuperViewModel


    private var _binding:ListSearchProductsFragmentBinding?=null
    val binding get() = _binding!!

    val items = ArrayList<Item>()
    var conditionSearch:Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = ListSearchProductsFragmentBinding.inflate(layoutInflater,container,false)
        viewModel = ViewModelProvider(this).get(FindTheCheapestSuperViewModel::class.java)

        setMainActivityViewModel(requireActivity())


        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getDuplicateItemsFromAllSupers(mainActivityViewModel.db)
        observeItemsForAllSupers()

        initUIForSearchButtons()




        binding.btnSearchAllSupers.setOnClickListener {
            it.isSelected=it.isSelected.not()
            if(it.isSelected){
                viewModel.getDuplicateItemsFromAllSupers(mainActivityViewModel.db,false,1)
                observeItemsForAllSupers()
            }else{
                viewModel.getDuplicateItemsFromAllSupers(mainActivityViewModel.db)
                observeItemsForAllSupers()
            }
        }


        binding.btnSearchShufersal.setOnClickListener {
            onBrandClicked(it)
        }
        binding.btnSearchVictory.setOnClickListener {
            onBrandClicked(it)
        }
        binding.btnSearchHCohen.setOnClickListener {
            onBrandClicked(it)
        }
        binding.btnSearchBareket.setOnClickListener {
            onBrandClicked(it)
        }
        binding.btnSearchMahsaniashok.setOnClickListener {
            onBrandClicked(it)
        }


        binding.rvChosenItems.layoutManager = LinearLayoutManager(requireContext())
        binding.rvSupersResult.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)

        binding.rvChosenItems.adapter = SingleProductRecycleView(items,this)
        binding.rvSupersResult.adapter = SuperAtBrandWithTotalPriceRecycleView(viewModel.brandAndStoreStore_ToPrice,this,items)



        binding.btnSearchForMatchesInAllUserSupers.setOnClickListener {
            binding.btnSearchForMatchesInAllUserSupers.delayOnLifeCycle(1000, view= binding.btnSearchForMatchesInAllUserSupers)
            onSearchSupersClick()
        }
    }

    private fun onSearchSupersClick() {
        viewModel.clear()
        binding.rvSupersResult.adapter!!.notifyDataSetChanged()
        if (items.isNotEmpty()) {
            conditionSearch =
                binding.btnSearchBareket.isSelected || binding.btnSearchHCohen.isSelected || binding.btnSearchShufersal.isSelected || binding.btnSearchVictory.isSelected || binding.btnSearchMahsaniashok.isSelected
            viewModel.getAvailableSupersFromItemList(
                items,
                mainActivityViewModel.db,
                conditionSearch
            )
        }
        viewModel.superAtBrand.observe(viewLifecycleOwner) {
            binding.rvSupersResult.adapter!!.notifyDataSetChanged()
        }
    }

    private fun onBrandClicked(it: View) {
        it.isSelected=it.isSelected.not()
        conditionSearch = binding.btnSearchBareket.isSelected||binding.btnSearchHCohen.isSelected||binding.btnSearchShufersal.isSelected||binding.btnSearchVictory.isSelected||binding.btnSearchMahsaniashok.isSelected
        if(it.isSelected){
            viewModel.setBrandIdForConditionSearch(it.contentDescription.toString())
        }else{
            viewModel.removeBrandIdForConditionSearch(it.contentDescription.toString())
        }

        if(conditionSearch){
            viewModel.getDuplicateItemsFromAllSupers(mainActivityViewModel.db,true)
            observeItemsForAllSupers()
        }else{
            viewModel.getDuplicateItemsFromAllSupers(mainActivityViewModel.db)
            observeItemsForAllSupers()
        }

        items.clear()
        viewModel.clear()
        binding.rvChosenItems.adapter?.notifyDataSetChanged()
        binding.rvSupersResult.adapter?.notifyDataSetChanged()
        binding.btnSearchForMatchesInAllUserSupers.performClick()

    }

    private fun observeItemsForAllSupers() {
        viewModel.itemFromAllSupers.observe(viewLifecycleOwner) {
            getObserverForSelectedItems(it)
            if(it.isEmpty()){
                return@observe
            }
            if(it.size<10){
                Toast.makeText(requireContext(), "no common items between your supers", Toast.LENGTH_SHORT).show()
                binding.btnSearchAllSupers.performClick()
            }
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
        items: List<Item>
    ) {
        val itemsFromSuper = ArrayList<Item>()
        lifecycleScope.launch(Dispatchers.IO) {
            items.forEach {
                val item: Item = mainActivityViewModel.db.FullItemTableDao()
                    .getItemFromStoreAtBrand(
                        superWithPrice.storeId_To_BrandId.storeId,
                        superWithPrice.storeId_To_BrandId.brandId,
                        it.itemName
                    )
                itemsFromSuper.add(item)
            }
        }.invokeOnCompletion {
            val fragment = SuperSearchFragment(
                itemsFromSuper,
                StoreId_To_BrandId(superWithPrice.storeId_To_BrandId.storeId,superWithPrice.storeId_To_BrandId.brandId)
            )
            parentFragmentManager.beginTransaction().replace(
                R.id.nav_host_fragment_activity_main_application, fragment
            ).addToBackStack(null).commit()

            viewModel.clear()
        }


    }
}