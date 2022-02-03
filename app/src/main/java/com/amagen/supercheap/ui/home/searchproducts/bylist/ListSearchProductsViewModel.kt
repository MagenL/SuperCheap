package com.amagen.supercheap.ui.home.searchproducts.bylist

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amagen.supercheap.database.ApplicationDB
import com.amagen.supercheap.models.BrandAndStore_toPrice
import com.amagen.supercheap.models.Item
import com.amagen.supercheap.models.StoreId_To_BrandId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListSearchProductsViewModel : ViewModel() {

    private val _itemFromAllSupers = MutableLiveData<List<Item>>()
    val itemFromAllSupers get() = _itemFromAllSupers

    private val _superAtBrand = MutableLiveData<List<StoreId_To_BrandId>>()
    val superAtBrand get() = _superAtBrand

    private val _mapStoreToPrice = HashMap<StoreId_To_BrandId, Double>()

    val brandAndStoreStore_ToPrice = ArrayList<BrandAndStore_toPrice>()

    private val _brandAndStore_toItemsMap = HashMap<StoreId_To_BrandId, List<Item>>()
    val brandAndStore_toItemsMap get() = _brandAndStore_toItemsMap

    fun clear(){
        _mapStoreToPrice.clear()
        brandAndStoreStore_ToPrice.clear()
        _brandAndStore_toItemsMap.clear()
    }


    fun getDuplicateItemsFromAllSupers(db:ApplicationDB){
        viewModelScope.launch(Dispatchers.IO) {
            _itemFromAllSupers.postValue(db.FullItemTableDao().countItemNames())

        }
    }
    fun getAvailableSupersFromItemList(items:List<Item>, db:ApplicationDB){
        val _storeAndID = ArrayList<StoreId_To_BrandId>()
        val storeAndIDasSET = ArrayList<StoreId_To_BrandId>()

        viewModelScope.launch(Dispatchers.IO) {



            val supersWithMissingItems = ArrayList<StoreId_To_BrandId>()

            items.toSet().iterator().forEach {item->
                _storeAndID.addAll(db.FullItemTableDao().findAllSupersWithUserListItems(item.itemName))
                _storeAndID.toSet().forEach{
                    //--------------check if super's db contains the selected item----------------//
                    if(db.FullItemTableDao().getPriceFromSuper(it.storeId,it.brandId,item.itemName)==0.0 ||
                        db.FullItemTableDao().getPriceFromSuper(it.storeId,it.brandId,item.itemName).isNaN()){
                        Log.d("checkifnull", it.toString())
                        supersWithMissingItems.add(it)
                    }else{
                        //--------------check if super's db hasn't init yet ----------------//
                        if(_mapStoreToPrice[it]==null){
                            Log.d("superAtBrandTOprice", " ${it.storeId} has initialized now")
                            _mapStoreToPrice[it]=0.0
                        }
                        //--------------add price to super----------------//

                        Log.d("superAtBrandTOprice", " ${it.storeId} at brand ${it.brandId} price ${db.FullItemTableDao().getPriceFromSuper(it.storeId,it.brandId,item.itemName)}")
                        _mapStoreToPrice[it]=_mapStoreToPrice[it]!!.toDouble() +db.FullItemTableDao().getPriceFromSuper(it.storeId,it.brandId,item.itemName)
                    }
                }



                supersWithMissingItems.toSet().forEach {
                    Log.d("superToRemove", "super ${it.storeId}")
                    _mapStoreToPrice.remove(it)
                }



            }
            storeAndIDasSET.addAll(_storeAndID.toSet())
            storeAndIDasSET.iterator().forEach {
                if(_mapStoreToPrice[StoreId_To_BrandId(it.storeId,it.brandId)]!=null){
                    brandAndStoreStore_ToPrice.add(
                        BrandAndStore_toPrice(
                            StoreId_To_BrandId(it.storeId,it.brandId),
                            db.superTableOfIdAndName()
                                .getStoreNameByBrandAndStoreId(it.storeId,it.brandId),
                            _mapStoreToPrice[it]!!

                        )
                    )
                    println("last = $it")
                }
            }
            Log.d("superAtBrandTOprice", " $brandAndStoreStore_ToPrice")
            _superAtBrand.postValue(storeAndIDasSET)
            _storeAndID.clear()
        }

    }


}