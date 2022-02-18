package com.amagen.supercheap.ui.home.searchproducts.bylist

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.amagen.supercheap.database.ApplicationDB
import com.amagen.supercheap.models.BrandAndStore_toPrice
import com.amagen.supercheap.models.BrandToId
import com.amagen.supercheap.models.Item
import com.amagen.supercheap.models.StoreId_To_BrandId
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ListSearchProductsViewModel : ViewModel() {

    private var _itemFromAllSupers = MutableLiveData<List<Item>>()
    val itemFromAllSupers get() = _itemFromAllSupers



    private val _superAtBrand = MutableLiveData<List<StoreId_To_BrandId>>()
    val superAtBrand get() = _superAtBrand

    private val _mapStoreToPrice = HashMap<StoreId_To_BrandId, Double>()

    val brandAndStoreStore_ToPrice = ArrayList<BrandAndStore_toPrice>()



    private val _userBrands= MutableLiveData<List<Int>>()
    val userBrands get() = _userBrands

    private val searchTerms = StringBuilder()
    private val brandToFilter =ArrayList<Int>()

    var shufersal:Int=0
    var victory:Int=0
    var hcohen:Int=0
    var mahsaniAshok:Int=0
    var bareket:Int=0

    private val tempArray = ArrayList<Item>()


    fun clear(){
        _mapStoreToPrice.clear()
        brandAndStoreStore_ToPrice.clear()
    }



    fun setBrandIdForConditionSearch(brandName:String){

        when(brandName){
            BrandToId.SHUFERSAL.brandName->{
                shufersal=BrandToId.SHUFERSAL.brandId
                println("shufersal ==== $shufersal")
            }
            BrandToId.VICTORY.brandName->{
                victory=BrandToId.VICTORY.brandId
            }
            BrandToId.SuperBareket.brandName->{
                bareket=BrandToId.SuperBareket.brandId
            }
            BrandToId.HCohen.brandName->{
                hcohen=BrandToId.HCohen.brandId
            }
            BrandToId.MahsaniAshok.brandName->{
                mahsaniAshok=BrandToId.MahsaniAshok.brandId
            }
        }
    }


    fun getDuplicateItemsFromAllSupers(db:ApplicationDB,
                                       condition:Boolean=false, times:Int=0
    ){
        _itemFromAllSupers= MutableLiveData<List<Item>>()
        viewModelScope.launch(Dispatchers.IO) {
            if(condition){
                println("condition detected cleaning up list")
                println(condition)

                println(_itemFromAllSupers.value.toString())
                println("shufersal ->$shufersal victory -> $victory mahsaniAshok ->$mahsaniAshok bareket -> $bareket hcohen -> $hcohen")
                _itemFromAllSupers.postValue(db.FullItemTableDao().getItemsByBrand(shufersal,victory,hcohen,mahsaniAshok,bareket))

            }else{
                if(times>0){
                    _itemFromAllSupers.postValue(db.FullItemTableDao().countItemNames(db.FullItemTableDao().getUserStoresCount()))
                }else{
                    _itemFromAllSupers.postValue(db.FullItemTableDao().countItemNames(times))
                }
            }
        }.invokeOnCompletion {
            println("downloaded data ")
        }
    }
    fun getAvailableSupersFromItemList(items:List<Item>, db:ApplicationDB,condition: Boolean=false){
        val _storeAndID = ArrayList<StoreId_To_BrandId>()
        val storeAndIDasSET = ArrayList<StoreId_To_BrandId>()

        viewModelScope.launch(Dispatchers.IO) {
            val supersWithMissingItems = ArrayList<StoreId_To_BrandId>()

            items.toSet().iterator().forEach {item->
                if(condition){
                    _storeAndID.addAll(db.FullItemTableDao().findAllSupersWithUserListItems(item.itemName,shufersal,victory,hcohen,mahsaniAshok,bareket))
                }else{
                    _storeAndID.addAll(db.FullItemTableDao().findAllSupersWithUserListItems(item.itemName))
                }
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
                                .getStoreNameByBrandAndStoreIdUserFavTable(it.storeId,it.brandId),
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
    fun getAllBrands(db:ApplicationDB){
        viewModelScope.launch(Dispatchers.IO) {
            _userBrands.postValue(db.superTableOfIdAndName().getUserFavBrands())
        }
    }

    fun clearTerms() {
        searchTerms.clear()
    }


}