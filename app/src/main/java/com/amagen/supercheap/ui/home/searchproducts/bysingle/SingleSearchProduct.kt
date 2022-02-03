package com.amagen.supercheap.ui.home.searchproducts.bysingle

import android.R
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.amagen.supercheap.MainActivityViewModel
import com.amagen.supercheap.databinding.SingleSearchProductFragmentBinding

import com.amagen.supercheap.models.Item
import com.amagen.supercheap.models.StoreId_To_BrandId
import com.amagen.supercheap.models.UserFavouriteSupers
import com.amagen.supercheap.ui.FunctionalFragment
import com.amagen.supercheap.recycleview.SingleProductRecycleView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

class SingleSearchProduct(val items:List<Item>?=null,val storeidToBrandid: StoreId_To_BrandId?=null) : FunctionalFragment(), SingleProductRecycleView.OnItemClickListener{

    private var _binding :SingleSearchProductFragmentBinding?=null
    val binding get() = _binding!!
    private lateinit var viewModel: SingleSearchProductViewModel

    private lateinit var mainActivityViewModel: MainActivityViewModel
    private val selecetedItems =ArrayList<Item>()

    private val mAuth:FirebaseAuth= FirebaseAuth.getInstance()
    private val fbProductsReference:DatabaseReference = FirebaseDatabase.getInstance().reference.child("listOfProducts").push()
    private val fbUserReference:DatabaseReference=FirebaseDatabase.getInstance().reference.child("users").child(mAuth.currentUser?.uid.toString())
    private var currentlySuper:Int=-1

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewModel = ViewModelProvider(this).get(SingleSearchProductViewModel::class.java)
        _binding = SingleSearchProductFragmentBinding.inflate(layoutInflater)
        mainActivityViewModel = ViewModelProvider(requireActivity())[MainActivityViewModel::class.java]
        return binding.root
    }





    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getAllUserSupers(mainActivityViewModel.db)




        if(items!=null){
            initUIPageWithItems()
        }
        binding.rvSingleItem.adapter= SingleProductRecycleView(selecetedItems,this)
        binding.rvSingleItem.layoutManager= LinearLayoutManager(requireContext())


        //-------------------------find supers-------------------------//
        viewModel.supersLink.observe(viewLifecycleOwner){
            findAllSupers_andSetAdapter(it)
        }

        //-------------------------add list to firebase-------------------------//


        binding.btnUpList.setOnClickListener {
            fbUserReference.child("shopping").push().setValue(selecetedItems)
                .addOnCompleteListener {
                    if(it.isSuccessful && it.isComplete){
                        Toast.makeText(requireContext(),"list uploaded successfully",Toast.LENGTH_SHORT).show()
                        selecetedItems.clear()
                        binding.rvSingleItem.adapter?.notifyDataSetChanged()
                    }
                }
                .addOnFailureListener {
                    Toast.makeText(requireContext(),""+it.localizedMessage,Toast.LENGTH_SHORT).show()
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

            whenSuperIsSelected_searchOrInseretItems(mySuper)
        }
    }

    private fun whenSuperIsSelected_searchOrInseretItems(mySuper: UserFavouriteSupers?) {
        onUserChangeSuperCleanList(mySuper!!.storeId)
        if(binding.searchSuper.text.isEmpty()){
            binding.searchSuper.setText(mySuper!!.superName)
        }
        lifecycleScope.launch(Dispatchers.IO) {
            if (mainActivityViewModel.db.FullItemTableDao()
                    .getShufersalTableById(mySuper!!.storeId).isEmpty()
            ) {

                mainActivityViewModel.createSuperItemsTable(mySuper.storeId, findBrand(mySuper.brand))
            } else {
                Log.d("dbChecker", "this super db is filled already")
                viewModel.getSuperTableById(mySuper.storeId, mainActivityViewModel.db)
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
                        mySuper!!.storeId,
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
        //move to viemodel til->
        lifecycleScope.launch(Dispatchers.IO) {
            val userFavouriteSuper=mainActivityViewModel.db.superTableOfIdAndName().getUserFavSuperById(storeidToBrandid!!.storeId,storeidToBrandid.brandId)
            currentlySuper = storeidToBrandid.storeId
            items!!.forEach {
                val item: Item = mainActivityViewModel.db.FullItemTableDao()
                    .getItemFromStoreAtBrand(
                        userFavouriteSuper.storeId,
                        userFavouriteSuper.brand,
                        it.itemName
                    )
                selecetedItems.add(item)
            }
       //until.
            lifecycleScope.launch(Dispatchers.Main) {

                whenSuperIsSelected_searchOrInseretItems(userFavouriteSuper)
                binding.rvSingleItem.adapter?.notifyDataSetChanged()
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


//object callback : (Boolean) -> Unit {
//    override fun invoke(p1: Boolean) {
//        Log.d("onCallbackInvoked", "invoke: ")
//    }
//
//}
