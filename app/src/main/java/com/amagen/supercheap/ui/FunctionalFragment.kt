package com.amagen.supercheap.ui

import android.app.Dialog
import android.util.Log
import android.view.View
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import com.amagen.supercheap.MainActivityViewModel
import com.amagen.supercheap.databinding.ListSearchProductsFragmentBinding
import com.amagen.supercheap.databinding.SingleSearchProductFragmentBinding
import com.amagen.supercheap.extensions.hideCorners
import com.amagen.supercheap.extensions.setDialogIfApplicationLoadingData
import com.amagen.supercheap.extensions.useNotToOppsisteZeroAndOne
import com.amagen.supercheap.models.BrandToId
import com.amagen.supercheap.models.IdToSuperName
import com.amagen.supercheap.models.Item
import com.amagen.supercheap.models.UserFavouriteSupers
import kotlinx.android.synthetic.main.item_dialog.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList

open class FunctionalFragment: Fragment() {

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
            UISuperName(dbSuperNames, it)
        }
        fvsupers?.map{
            UISuperName(dbSuperNames, it)
        }
    }

    fun UIUserFavSuper(userSuper:String,brand:Int):String{
        when(brand){
            BrandToId.SHUFERSAL.brandId->{
                if (!userSuper.contains(BrandToId.SHUFERSAL.brandName.toString())) {
                    return "${BrandToId.SHUFERSAL.brandName.toString()} ${userSuper.filter { char -> !char.isDigit() }}"
                }
            }
            BrandToId.VICTORY.brandId->{
                if (!userSuper.contains(BrandToId.VICTORY.brandName.toString())) {
                    return "${BrandToId.VICTORY.brandName.toString()} ${userSuper.filter { char -> !char.isDigit() }}"
                }
            }
            BrandToId.MahsaniAshok.brandId->{
                if (!userSuper.contains(BrandToId.MahsaniAshok.brandName.toString())) {
                    return "${BrandToId.MahsaniAshok.brandName.toString()} ${userSuper.filter { char -> !char.isDigit() }}"
                }
            }
            BrandToId.SuperBareket.brandId->{
                if (!userSuper.contains(BrandToId.SuperBareket.brandName.toString())) {
                    return "${BrandToId.SuperBareket.brandName.toString()} ${userSuper.filter { char -> !char.isDigit() }}"
                }
            }
            BrandToId.HCohen.brandId->{
                if (!userSuper.contains(BrandToId.HCohen.brandName.toString())) {
                    return "${BrandToId.HCohen.brandName.toString()} ${userSuper.filter { char -> !char.isDigit() }}"
                }
            }
        }
        return userSuper.filter { char-> !char.isDigit() }
    }

    private fun UISuperName(
        dbSuperNames: ArrayList<String>,
        it: IdToSuperName
    ) {
        val currentSuper = StringBuilder()
        dbSuperNames.add(it.superName)
        Log.d("dashboard", "old name: ${it.superName} ")
        currentSuper.append(UIUserFavSuper(it.superName,it.brand))
//        when(it.brand){
//            BrandToId.SHUFERSAL.brandId->{
//                if (!it.superName.contains(BrandToId.SHUFERSAL.brandName.toString())) {
//                    currentSuper.append(BrandToId.SHUFERSAL.brandName.toString()).append(" ")
//                }
//            }
//            BrandToId.VICTORY.brandId->{
//                if (!it.superName.contains(BrandToId.VICTORY.brandName.toString())) {
//                    currentSuper.append(BrandToId.VICTORY.brandName.toString()).append(" ")
//                }
//            }
//            BrandToId.MahsaniAshok.brandId->{
//                if (!it.superName.contains(BrandToId.MahsaniAshok.brandName.toString())) {
//                    currentSuper.append(BrandToId.MahsaniAshok.brandName.toString()).append(" ")
//                }
//            }
//            BrandToId.SuperBareket.brandId->{
//                if (!it.superName.contains(BrandToId.SuperBareket.brandName.toString())) {
//                    currentSuper.append(BrandToId.SuperBareket.brandName.toString()).append(" ")
//                }
//            }
//            BrandToId.HCohen.brandId->{
//                if (!it.superName.contains(BrandToId.HCohen.brandName.toString())) {
//                    currentSuper.append(BrandToId.HCohen.brandName.toString()).append(" ")
//                }
//            }
//        }
//        currentSuper.append(it.superName.filter { char -> !char.isDigit() })
        it.superName = currentSuper.toString()
        Log.d("dashboard", "new name: ${it.superName} ")
    }

    fun UISuperName(
        dbSuperNames: ArrayList<String>,
        it: UserFavouriteSupers
    ) {
        val currentSuper = StringBuilder()
        dbSuperNames.add(it.superName)
        Log.d("dashboard", "old name: ${it.superName} ")
        currentSuper.append(UIUserFavSuper(it.superName,it.brand))

        it.superName = currentSuper.toString()
        Log.d("dashboard", "new name: ${it.superName} ")

//        when(it.brand){
//            BrandToId.SHUFERSAL.brandId->{
//                if (!it.superName.contains(BrandToId.SHUFERSAL.brandName.toString())) {
//                    currentSuper.append(BrandToId.SHUFERSAL.brandName.toString()).append(" ")
//                }
//            }
//            BrandToId.VICTORY.brandId->{
//                if (!it.superName.contains(BrandToId.VICTORY.brandName.toString())) {
//                    currentSuper.append(BrandToId.VICTORY.brandName.toString()).append(" ")
//                }
//            }
//            BrandToId.MahsaniAshok.brandId->{
//                if (!it.superName.contains(BrandToId.MahsaniAshok.brandName.toString())) {
//                    currentSuper.append(BrandToId.MahsaniAshok.brandName.toString()).append(" ")
//                }
//            }
//            BrandToId.SuperBareket.brandId->{
//                if (!it.superName.contains(BrandToId.SuperBareket.brandName.toString())) {
//                    currentSuper.append(BrandToId.SuperBareket.brandName.toString()).append(" ")
//                }
//            }
//            BrandToId.HCohen.brandId->{
//                if (!it.superName.contains(BrandToId.HCohen.brandName.toString())) {
//                    currentSuper.append(BrandToId.HCohen.brandName.toString()).append(" ")
//                }
//            }
//        }
//        currentSuper.append(it.superName.filter { char -> !char.isDigit() })


//        val currentSuper = StringBuilder()
//        dbSuperNames.add(it.superName)
//        Log.d("dashboard", "old name: ${it.superName} ")
//        if (!it.superName.contains("שופרסל")) {
//            currentSuper.append("שופרסל").append(" ")
//        }
//        currentSuper.append(it.superName.filter { char -> !char.isDigit() })
//        Log.d("dashboard", "new name: ${it.superName} ")
//        it.superName = currentSuper.toString()
    }


    fun checkLastSuperDbUpdate(superDetails: UserFavouriteSupers?, mainActivityViewModel: MainActivityViewModel, btnUpdate:Button, direction:Int?) {
        val twentyFourHoursInMillis=86_400_000

        lifecycleScope.launch(Dispatchers.IO) {
            val dateFromDbPlus24H = mainActivityViewModel.db.superTableOfIdAndName().getLastUpdate()+twentyFourHoursInMillis
            //-------------------------first time super has implemented to user-------------------------//
            if(mainActivityViewModel.db.superTableOfIdAndName().getLastUpdate().toInt() == 0){

                mainActivityViewModel.db.superTableOfIdAndName().updateDateOfItemsDB(Calendar.getInstance().timeInMillis,superDetails!!.storeId,superDetails.brand)
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
                            Log.d(TAG, ": starting the update!")
                            mainActivityViewModel.db.FullItemTableDao().deleteSuperTable(superDetails!!.storeId,superDetails.brand)

                            lifecycleScope.launch(Dispatchers.Main){
                                checkIfFragmentLoadingData(mainActivityViewModel.downloadAndCreateSuperTableProcess)
                            }

                            mainActivityViewModel.createSuperItemsTable(
                                superDetails.storeId,
                                findBrand(superDetails.brand)
                            )
                            mainActivityViewModel.db.superTableOfIdAndName().updateDateOfItemsDB(
                                Calendar.getInstance().timeInMillis,superDetails.storeId,superDetails.brand)
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

}