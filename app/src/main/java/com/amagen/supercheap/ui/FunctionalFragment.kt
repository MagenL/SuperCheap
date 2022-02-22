package com.amagen.supercheap.ui

import android.app.Dialog
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import com.amagen.supercheap.MainActivityViewModel
import com.amagen.supercheap.R
import com.amagen.supercheap.databinding.ListSearchProductsFragmentBinding
import com.amagen.supercheap.databinding.SingleSearchProductFragmentBinding
import com.amagen.supercheap.extensions.hideCorners
import com.amagen.supercheap.extensions.setDialogIfApplicationLoadingData
import com.amagen.supercheap.extensions.useNotToOppsisteZeroAndOne
import com.amagen.supercheap.models.*
import kotlinx.android.synthetic.main.item_dialog.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.IllegalArgumentException
import java.net.ConnectException
import java.util.*
import kotlin.collections.ArrayList

open class FunctionalFragment(): Fragment() {
    private var _mainActivityViewModel:MainActivityViewModel?=null
    val mainActivityViewModel get() = _mainActivityViewModel!!

    fun setMainActivityViewModel(mActivity:ViewModelStoreOwner){
        _mainActivityViewModel = ViewModelProvider(mActivity)[MainActivityViewModel::class.java]
    }

    fun dialogToItem(
        itemPosition: Int,
        item: Item,
        singleSearchBinding: SingleSearchProductFragmentBinding?=null,
        listSearchBinding:ListSearchProductsFragmentBinding?=null,
        selecetedItems: ArrayList<Item>
    ) {
        if(singleSearchBinding==null &&listSearchBinding ==null){
            return
        }

        val dialog = Dialog(requireContext())
        dialog.setContentView(com.amagen.supercheap.R.layout.item_dialog)
        //dialog.window?.setBackgroundDrawableResource(R.color.transparent)
        dialog.hideCorners()
        dialog.btn_ok.setOnClickListener {
            dialog.dismiss()
        }
        dialog.btn_remove.setOnClickListener {
            selecetedItems.removeAt(itemPosition)
            if(singleSearchBinding!=null){
                singleSearchBinding.rvSingleItem.adapter?.notifyItemRemoved(itemPosition)
            }else{
                listSearchBinding!!.rvChosenItems.adapter?.notifyItemRemoved(itemPosition)
                listSearchBinding.btnSearchForMatchesInAllUserSupers.performClick()

            }

            dialog.dismiss()
        }

        dialog.item_name.text = item.itemName
        dialog.tv_manufacturer_name.text = resources.getString(
            com.amagen.supercheap.R.string.manufacturer_name,
            item.manufacturerName
        )
        dialog.tv_manufacturer_country.text = resources.getString(
            com.amagen.supercheap.R.string.manufacturer_name,
            item.manufacturerCountry
        )
        dialog.tv_unit_of_measure.text = item.unitOfMeasure
        dialog.tv_price_to_unit.text = item.unitOfMeasurePrice.toString()
        dialog.tv_total_price.text =
            resources.getString(com.amagen.supercheap.R.string.price_to_unit_f, item.itemPrice)
        dialog.show()
    }



    fun checkIfFragmentLoadingData(loading: LiveData<Boolean>,dialog:Dialog=Dialog(requireContext())){
        requireContext().
        setDialogIfApplicationLoadingData(
            loading,
            dialog,
            viewLifecycleOwner
        )

    }
    fun findBrand(id: Int): BrandToId {
        for (brandToId in BrandToId.values()){
            if(brandToId.brandId==id)
                return brandToId
        }
        return BrandToId.SHUFERSAL
    }

    fun supersUIname(
        supers: List<IdToSuperName>?=null,
        dbSuperNames: ArrayList<String>,
        fvsupers:List<UserFavouriteSupers>?=null
    ) {
        supers?.map {
            mainActivityViewModel.UISuperName(dbSuperNames, null, it)
        }
        fvsupers?.map{
            mainActivityViewModel.UISuperName(dbSuperNames, it, null)
        }
    }





    fun checkLastSuperDbUpdate(superDetails: StoreId_To_BrandId, mainActivityViewModel: MainActivityViewModel, btnUpdate:Button, direction:Int?) {
        val twentyFourHoursInMillis=86_400_000

        lifecycleScope.launch(Dispatchers.IO) {
            val dateFromDbPlus24H = mainActivityViewModel.db.superTableOfIdAndName().getLastUpdate()+twentyFourHoursInMillis
            //-------------------------first time super has implemented to user-------------------------//
            if(mainActivityViewModel.db.superTableOfIdAndName().getLastUpdate().toInt() == 0){

                mainActivityViewModel.db.superTableOfIdAndName().updateDateOfItemsDB(Calendar.getInstance().timeInMillis,superDetails!!.storeId,superDetails.brandId)
                Log.d("dbDateTime", "db updated successfully")
            }else{
                //-------------------------if super table is not update to the last 24h-------------------------//
                if(Calendar.getInstance().timeInMillis>dateFromDbPlus24H){
                    val TAG="updaterser"
                    Log.d(TAG, ": update needed!")
                    lifecycleScope.launch(Dispatchers.Main) {
                        btnUpdate.visibility= View.VISIBLE
                        if (direction != null) {
                            btnUpdate.layoutDirection= direction.useNotToOppsisteZeroAndOne()
                        }else{
                            btnUpdate.layoutDirection=View.LAYOUT_DIRECTION_INHERIT
                        }
                    }
                    btnUpdate.setOnClickListener {
                        lifecycleScope.launch(Dispatchers.IO) {
                            getSuperLink(superDetails.storeId,findBrand(superDetails.brandId)) {link->

                                Log.d(TAG, ": starting the update!")
                                mainActivityViewModel.db.FullItemTableDao()
                                    .deleteSuperTable(superDetails.storeId, superDetails.brandId)

                                lifecycleScope.launch(Dispatchers.Main) {
                                    checkIfFragmentLoadingData(mainActivityViewModel.downloadAndCreateSuperTableProcess)
                                }
                                mainActivityViewModel.createSuperItemsTable(
                                    superDetails.storeId,
                                    findBrand(superDetails.brandId),
                                    link!!
                                )
                                mainActivityViewModel.db.superTableOfIdAndName().updateDateOfItemsDB(
                                    Calendar.getInstance().timeInMillis,superDetails.storeId,superDetails.brandId)
                            }

                        }.invokeOnCompletion {
                            lifecycleScope.launch(Dispatchers.Main) {
                                Log.d(TAG, ": update finished!")
                                btnUpdate.visibility= View.GONE
                            }
                        }


                    }


                }
            }

        }

    }

    fun getSuperLink(storeId:Int,brandToId:BrandToId, blockToRunIfLinkIsValid:(link:String?)->Unit){
        var alink:String? =null
        lifecycleScope.launch(Dispatchers.IO) {
            lifecycleScope.launch(Dispatchers.Main) {
                checkIfFragmentLoadingData(mainActivityViewModel.downloadAndCreateSuperTableProcess)
            }
            val link = mainActivityViewModel.getNewLink(storeId,brandToId)
            alink = link
        }.invokeOnCompletion {
            if(it != null){
                Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_SHORT).show()
            }else{
                lifecycleScope.launch(Dispatchers.IO) {
                    blockToRunIfLinkIsValid(alink)
                }

            }
        }
    }


}