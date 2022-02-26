package com.amagen.supercheap.ui.home.searchproducts.singleSuperSearch

import android.R
import android.app.Dialog
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.LiveData

import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.amagen.supercheap.databinding.SingleSearchProductFragmentBinding
import com.amagen.supercheap.exceptions.MyExceptions
import com.amagen.supercheap.extensions.checkConnectivityStatus
import com.amagen.supercheap.extensions.delayOnLifeCycle

import com.amagen.supercheap.models.Item
import com.amagen.supercheap.models.StoreId_To_BrandId
import com.amagen.supercheap.models.UserFavouriteSupers
import com.amagen.supercheap.ui.FunctionalFragment
import com.amagen.supercheap.recycleview.SingleProductRecycleView
import kotlinx.coroutines.CoroutineExceptionHandler

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.NullPointerException
import kotlin.collections.ArrayList

class SuperSearchFragment(val items:List<Item>?=null, val storeidToBrandid: StoreId_To_BrandId?=null) : FunctionalFragment(), SingleProductRecycleView.OnItemClickListener{

    private var _binding :SingleSearchProductFragmentBinding?=null
    val binding get() = _binding!!
    private lateinit var viewModel: SuperSearchViewModel

    private val selecetedItems =ArrayList<Item>()

    private var currentlySuper:Int=-1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewModel = ViewModelProvider(this)[SuperSearchViewModel::class.java]
        _binding = SingleSearchProductFragmentBinding.inflate(layoutInflater)
        setMainActivityViewModel(requireActivity())
        return binding.root
    }





    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getAllUserSupers(mainActivityViewModel.db)

        binding.rvSingleItem.adapter= SingleProductRecycleView(selecetedItems,this)
        binding.rvSingleItem.layoutManager= LinearLayoutManager(requireContext())


        if(storeidToBrandid!=null){
            initUIPageWithItems()

        }else{
            //-------------------------find supers-------------------------//
            viewModel.supersLink.observe(viewLifecycleOwner){
                findAllSupers_andSetAdapter(it)
            }
        }





        //-------------------------add list to firebase-------------------------//


        binding.btnUpList.setOnClickListener {
            var superName:String
            lifecycleScope.launch(Dispatchers.IO) {
                if(selecetedItems.isNotEmpty()){
                    superName = viewModel.getSuperName(mainActivityViewModel.db,StoreId_To_BrandId(selecetedItems[0].storeId,selecetedItems[0].brandId))
                    if(superName.isNotEmpty()){
                        it.delayOnLifeCycle(durationInMillis = 4000L,view= binding.btnUpList){
                            superName = mainActivityViewModel.UIUserFavSuper(superName,selecetedItems[0].brandId)
                            viewModel.uploadCartToDB(selecetedItems,superName)
                            lifecycleScope.launch(Dispatchers.Main) {
                                viewModel.uploadToFirebaseListener.observe(viewLifecycleOwner){
                                    if (it){
                                        Toast.makeText(requireContext(), "uploaded successfully", Toast.LENGTH_SHORT).show()
                                        binding.rvSingleItem.adapter!!.notifyItemRangeRemoved(0,selecetedItems.size)
                                        selecetedItems.clear()
                                    }
                                }
                            }
                        }

                    }
                }else{
                    lifecycleScope.launch(Dispatchers.Main){
                        Toast.makeText(requireContext(), "please fill super and list to upload", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


    }

    private fun findAllSupers_andSetAdapter(it: List<UserFavouriteSupers>) {
        val dbSuperNames = ArrayList<String>()

        supersUIname(fvsupers = it, dbSuperNames = dbSuperNames)

        val adapter = ArrayAdapter(
            requireContext(), R.layout.simple_expandable_list_item_1, it.map { it.superName })

        binding.searchSuper.setAdapter(adapter)
        binding.searchSuper.setOnItemClickListener { parent, view, position, id ->
            //-------------------------retrieve super object from super's string-------------------------//
            val mySuper = it.find { it.superName == parent.getItemAtPosition(position) }
            whenSuperIsSelected_searchOrInseretItems(StoreId_To_BrandId(mySuper!!.storeId,mySuper.brand))

        }
    }

    private fun whenSuperIsSelected_searchOrInseretItems(mySuper: StoreId_To_BrandId) {
        onUserChangeSuperCleanList(mySuper.storeId)
        lifecycleScope.launch(Dispatchers.IO) {
            if (mainActivityViewModel.db.FullItemTableDao()
                    .getSuperTableById(mySuper.storeId,mySuper.brandId).isEmpty()
            ) {
                Log.d("dbChecker", "creating new table")
                getSuperLink(mySuper.storeId,findBrand(mySuper.brandId)){link->
                    lifecycleScope.launch(Dispatchers.Main) {
                        checkIfFragmentLoadingData(mainActivityViewModel.downloadAndCreateSuperTableProcess)
                        lifecycleScope.launch(Dispatchers.IO+MyExceptions.exceptionHandlerForCoroutines(requireContext())) {
                            mainActivityViewModel.createSuperItemsTable(mySuper.storeId, findBrand(mySuper.brandId),link!!)
                        }.invokeOnCompletion {
                            if(it!=null){
                                Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_SHORT).show()
                            }else{
                                viewModel.addSuperToFavorite(
                                    StoreId_To_BrandId(mySuper.storeId, mySuper.brandId),
                                    mainActivityViewModel.db
                                )
                            }
                        }
                    }
                }
            } else {
                Log.d("dbChecker", "this super db is filled already")
                viewModel.getSuperTableById(mySuper.storeId,mySuper.brandId, mainActivityViewModel.db)
                checkLastSuperDbUpdate(
                    mySuper,
                    mainActivityViewModel,
                    binding.btnUpdate,
                    binding.searchSuper.layoutDirection
                )

            }
        }
        //-------------------------call animation of loading to interfaces the user on loading-------------------------//
        checkIfFragmentLoadingData(viewModel.loadingFragment)
        mainActivityViewModel.downloadAndCreateSuperTableProcess.observe(viewLifecycleOwner) {

            //-------------------------while looking for Items to arrive show loading-------------------------//
            if (!it) {
                //-------------------------get items in super-------------------------//
                lifecycleScope.launch(Dispatchers.IO) {
                    viewModel.getSuperTableById(
                        mySuper.storeId,
                        mySuper.brandId,
                        mainActivityViewModel.db
                    )
                }.invokeOnCompletion {
                    //-------------------------table of items has filled successfully-------------------------//
                    lifecycleScope.launch(Dispatchers.Main) {
                        viewModel.itemFromSuper.observe(viewLifecycleOwner) {
                            binding.searchProduct.setAdapter(
                                ArrayAdapter(
                                    requireContext(),
                                    R.layout.simple_expandable_list_item_1,
                                    it.map {
                                        it.itemName.toString()
                                    })
                            )

                            //-------------------------on user pick item, add it to list-------------------------//
                            binding.searchProduct.setOnItemClickListener { parent, view, position, id ->
                                val myItem = it.find { it.itemName == parent.getItemAtPosition(position) }
                                selecetedItems.add(myItem!!)
                                binding.rvSingleItem.adapter?.notifyItemInserted(selecetedItems.size - 1)
                                binding.searchProduct.setText("")
                                binding.btnUpList.visibility = View.VISIBLE

                            }
                        }
                    }
                }

            } else {
                checkIfFragmentLoadingData(mainActivityViewModel.downloadAndCreateSuperTableProcess)
            }

        }
    }

    private fun initUIPageWithItems() {
        checkIfFragmentLoadingData(mainActivityViewModel.loadingProcessForDownloadingSupers)
        mainActivityViewModel.loadingProcessForDownloadingSupers.observe(viewLifecycleOwner){
            if(!it){
                lifecycleScope.launch(Dispatchers.IO){
                    if(!mainActivityViewModel.db.superTableOfIdAndName()
                            .getStoreNameByBrandAndStoreIdGeneralTable(storeidToBrandid!!.storeId,storeidToBrandid.brandId)
                            .isNullOrEmpty()){
                        currentlySuper = storeidToBrandid.storeId
                        if(items!=null){
                            selecetedItems.addAll(items)
                        }
                        var superName=""
                        lifecycleScope.launch(Dispatchers.IO) {
                            superName = viewModel.getSuperName(mainActivityViewModel.db,storeidToBrandid.storeId,storeidToBrandid.brandId)
                            superName = mainActivityViewModel.UIUserFavSuper(superName,storeidToBrandid.brandId)

                        }.invokeOnCompletion {
                            try{
                                lifecycleScope.launch(Dispatchers.Main){
                                    binding.searchSuper.setText(superName)
                                    binding.searchSuper.isEnabled=false
                                    whenSuperIsSelected_searchOrInseretItems(storeidToBrandid)
                                }
                            }catch (e:NullPointerException){
                                println("super name not found")
                            }

                        }
                    }else{
                        // handling the situation when user have been terminated the supers download process
                        // or
                        // user's db is not up to date i.e not filling the the super it tries to access to

                        mainActivityViewModel.getAllSupers()
                        lifecycleScope.launch(Dispatchers.Main){
                            checkIfFragmentLoadingData(mainActivityViewModel.loadingProcessForDownloadingSupers)
                            mainActivityViewModel.loadingProcessForDownloadingSupers.observe(viewLifecycleOwner){
                                if(!it){
                                    initUIPageWithItems()
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    //-------------------------delete previous list if user changed super-------------------------//
    private fun onUserChangeSuperCleanList(
        idOfShufersalSuper: Int
    ) {
        if (currentlySuper != idOfShufersalSuper) {
            selecetedItems.clear()
            binding.rvSingleItem.adapter?.notifyDataSetChanged()
        }
        currentlySuper=idOfShufersalSuper
    }




    override fun onItemRootClick(item: Item, itemPosition: Int) {

        dialogToItem(itemPosition, item,binding,null,selecetedItems)

    }



    //-------------------------delete item from list on long click registered-------------------------//
    override fun onItemRootLongClick(item: Item,position:Int) {
        binding.rvSingleItem.adapter?.notifyItemRemoved(position)
        selecetedItems.removeAt(position)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding=null
    }
}