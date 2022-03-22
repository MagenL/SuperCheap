package com.amagen.supercheap

import android.util.Log
import androidx.lifecycle.*
import com.amagen.supercheap.exceptions.MyExceptions
import com.amagen.supercheap.models.*
import com.amagen.supercheap.webScrapping.ShufersalWebScrapping
import com.amagen.supercheap.webScrapping.ShufersalWebScrappingRepo
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.*
import org.json.JSONArray
import org.json.JSONObject
import java.util.*
import kotlin.coroutines.cancellation.CancellationException

class MainActivityViewModel() : ViewModel() {


    val db= MainActivityApplication.instance!!.db
    private val py: Python = MainActivityApplication.pyInstance!!


    private var _downloadAndCreateSuperTableProcess :MutableLiveData<Boolean> = MutableLiveData(false)
    val downloadAndCreateSuperTableProcess:LiveData<Boolean> get() = _downloadAndCreateSuperTableProcess


    private var _loadingProcessForDownloadingSupers :MutableLiveData<Boolean> = MutableLiveData(false)
    val loadingProcessForDownloadingSupers:LiveData<Boolean> get() = _loadingProcessForDownloadingSupers


    private val itemOptionList = MutableLiveData<List<Item>>()
    val itemFromSuper: LiveData<List<Item>> = itemOptionList

    private var superNameAndId= MutableLiveData<List<IdToSuperName>>()
    val listOfSupers get() = superNameAndId





    fun createSuperItemsTable(superId: Int, brand: BrandToId,link:String){

        if(_downloadAndCreateSuperTableProcess.value == false){
            viewModelScope.launch(Dispatchers.Main){
                _downloadAndCreateSuperTableProcess.value=true
            }.invokeOnCompletion {
                viewModelScope.launch(Dispatchers.IO+MyExceptions.exceptionHandlerForCoroutinesInViewModel()) {
                        val callbackObject: PyObject = py
                            .getModule("python_service_gz_to_json")
                            .callAttr("download", link)

                        if (brand == BrandToId.SHUFERSAL) {
                            val itemsJson = JSONObject(callbackObject.toString())
                                .getJSONObject("root")
                                .getJSONObject("Items")
                                .getJSONArray("Item")
                            jsonToSQLTable(itemsJson, superId, brand.brandId)
                        } else {
                            val itemsJson = JSONObject(callbackObject.toString())
                                .getJSONObject("Prices")
                                .getJSONObject("Products")
                                .getJSONArray("Product")
                            jsonToSQLTable(itemsJson, superId, brand.brandId)
                        }

                        //check if the db contains dublicated items
                        val arr = db.FullItemTableDao().getAllDuplicateRows(superId)
                        db.FullItemTableDao().deleteAllDuplicateRows(superId)
                        db.FullItemTableDao().insertDeletedDuplicatedRows(arr)



                }.invokeOnCompletion {
                    if(it!=null){
                        println(it.localizedMessage)
                    }
                    viewModelScope.launch(Dispatchers.Main) {
                        _downloadAndCreateSuperTableProcess.value = false
                        //check if table is in favorite
                    }
                }

            }
        }
    }

    private suspend fun jsonToSQLTable(
        itemsJson: JSONArray,
        superId: Int,
        brandId: Int
    ) {
        for (i in 0 until itemsJson.length()) {
            itemsJson.getJSONObject(i).put("storeId", superId)
            itemsJson.getJSONObject(i).put("brandId", brandId)
            if(itemsJson.getJSONObject(i).get("ItemCode").toString().toIntOrNull() ==null ){
                itemsJson.getJSONObject(i).put("ItemCode",0)
            }

        }
        db.FullItemTableDao().upsertTableItem(
            Gson().fromJson(
                itemsJson.toString(),
                TypeToken.getParameterized(List::class.java, Item::class.java).type
            )

        )
    }

    fun getNewLink(superId: Int, brand: BrandToId):String {
        _downloadAndCreateSuperTableProcess.postValue(true)
        try{
            if (brand == BrandToId.SHUFERSAL) {
                val link = py.getModule("findSuperLinkByIdDirect")
                    .callAttr("finder", brand.storeIdBaseURL, superId)
                    .toString()
                if (link.isNullOrEmpty()) {
                    throw CancellationException(MainActivityApplication.instance!!.resources.getString(R.string.server_in_maintenance_please_try_again_later))

                }
                _downloadAndCreateSuperTableProcess.postValue(false)
                return link

            } else {
                val link = py.getModule("find_vic_super_by_id")
                    .callAttr("getLinkByID", brand.priceBaseURL, superId).toString()
                if (link.isNullOrEmpty()) {
                    throw CancellationException(MainActivityApplication.instance!!.resources.getString(R.string.server_in_maintenance_please_try_again_later))
                }
                return link
            }
        }finally {
            _downloadAndCreateSuperTableProcess.postValue(false)
        }

    }

    fun getAllSupers() {
        viewModelScope.launch(Dispatchers.Main) {
            _loadingProcessForDownloadingSupers.value = true
        }

        viewModelScope.launch(Dispatchers.IO) {
            if (db.superTableOfIdAndName().getAllSupers().isEmpty()) {
                //shufersal
                val TAG = "MainActivityViewModel"
                Log.d(TAG, "db is empty ")

                val shufersalJob = CoroutineScope(Dispatchers.IO).launch {
                    db.superTableOfIdAndName().upsertTable(getShufersalTable())
                }


                //               victory

                val victoryJob = CoroutineScope(Dispatchers.IO).launch {
                    val pyVictoryBrandsIdAndName = py.getModule("victory_scrapping")
                        .callAttr("starter", BrandToId.VICTORY.priceBaseURL)
                    val jsonArrayOfVictoryItems = JSONArray(pyVictoryBrandsIdAndName.toString())
                    db.superTableOfIdAndName().upsertTable(
                        Gson().fromJson(
                            jsonArrayOfVictoryItems.toString(),
                            TypeToken.getParameterized(
                                List::class.java,
                                IdToSuperName::class.java
                            ).type
                        )
                    )

                }

                shufersalJob.start()
                victoryJob.start()
                shufersalJob.join()
                victoryJob.join()


            }
        }.invokeOnCompletion {
            viewModelScope.launch(Dispatchers.IO) {
                superNameAndId.postValue(db.superTableOfIdAndName().getAllSupers())
            }
            viewModelScope.launch(Dispatchers.Main) {
                _loadingProcessForDownloadingSupers.value = false
            }

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




    fun UISuperName(
        dbSuperNames: ArrayList<String>,
        userFavouriteSupers: UserFavouriteSupers?=null,
        idToSuperName: IdToSuperName?=null
    ) {
        val currentSuper = StringBuilder()
        if(userFavouriteSupers != null){
            dbSuperNames.add(userFavouriteSupers.superName)
            currentSuper.append(UIUserFavSuper(userFavouriteSupers.superName,userFavouriteSupers.brand))
            userFavouriteSupers.superName = currentSuper.toString()
        }else{
            dbSuperNames.add(idToSuperName!!.superName)
            currentSuper.append(UIUserFavSuper(idToSuperName.superName,idToSuperName.brand))
            idToSuperName.superName = currentSuper.toString()
        }


    }

    private suspend fun getShufersalTable():List<IdToSuperName>{
        return withContext(Dispatchers.IO){
            ShufersalWebScrappingRepo.getShufersalDetails()
        }

    }




}