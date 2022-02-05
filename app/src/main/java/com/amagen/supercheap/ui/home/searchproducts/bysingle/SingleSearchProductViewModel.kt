package com.amagen.supercheap.ui.home.searchproducts.bysingle

import android.app.Application
import androidx.lifecycle.*
import com.amagen.supercheap.database.ApplicationDB
import com.amagen.supercheap.models.Item
import com.amagen.supercheap.models.StoreId_To_BrandId
import com.amagen.supercheap.models.UserFavouriteSupers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SingleSearchProductViewModel(application:Application) : AndroidViewModel(application) {

    private var linkToSuper = MutableLiveData<List<UserFavouriteSupers>>()
    val supersLink:LiveData<List<UserFavouriteSupers>>  get() =  linkToSuper

    private var itemName = MutableLiveData<List<Item>>()
    val itemFromSuper get() = itemName

    private var loadingSingleSearchMLD = MutableLiveData(false)
    val loadingFragment get() = loadingSingleSearchMLD



    fun getAllUserSupers(db: ApplicationDB){
        viewModelScope.launch(Dispatchers.IO) {
            linkToSuper.postValue(db.superTableOfIdAndName().getAllUserFavSupers())
        }
    }
    fun getSuperTableById(superId:Int, superBrand:Int, db: ApplicationDB){
        loadingSingleSearchMLD.postValue(true)
        itemName.postValue(db.FullItemTableDao().getShufersalTableById(superId, superBrand ))
        loadingSingleSearchMLD.postValue(false)
    }

//    if you want to turn it back on, you got to transfer brand id into deletesupertable function.
//    fun deletesOldSuperTable(superId: Int, shufersalTableDao: ShufersalDao){
//        shufersalTableDao.deleteSuperTable(superId)
//    }

    fun getSuperName(db: ApplicationDB,storeIdAndBrandId: StoreId_To_BrandId):String{
        return db.superTableOfIdAndName().getStoreNameByBrandAndStoreId(storeIdAndBrandId.storeId,storeIdAndBrandId.brandId)
    }

}