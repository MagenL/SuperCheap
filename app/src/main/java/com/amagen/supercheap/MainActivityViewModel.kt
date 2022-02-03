package com.amagen.supercheap

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.amagen.supercheap.database.ApplicationDB
import com.amagen.supercheap.models.BrandToId
import com.amagen.supercheap.models.IdToSuperName
import com.amagen.supercheap.models.Item
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {
    val db = ApplicationDB.create(application)
    val py: Python

    private var _downloadAndCreateSuperTableProcess :MutableLiveData<Boolean> = MutableLiveData(false)
    val downloadAndCreateSuperTableProcess:LiveData<Boolean> get() = _downloadAndCreateSuperTableProcess


    private var dashboardLoading :MutableLiveData<Boolean> = MutableLiveData(false)
    val loadingProcessForDashboardFragment:LiveData<Boolean> get() = dashboardLoading


    private val itemOptionList = MutableLiveData<List<Item>>()
    val itemFromSuper: LiveData<List<Item>> = itemOptionList

    private var superNameAndId= MutableLiveData<List<IdToSuperName>>()
    val listOfSupers get() = superNameAndId


    init {
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(application))
        }
        py = Python.getInstance()

    }

    fun createTableWithSuperURL(url: String) {
        viewModelScope.launch(Dispatchers.IO) {


            val callbackObject: PyObject =
                py.getModule("python_service_gz_to_json").callAttr("download", url)
            val itemsJson =
                JSONObject(callbackObject.toString()).getJSONObject("root").getJSONObject("Items")
                    .getJSONArray("Item")
            val storeJson =
                JSONObject(callbackObject.toString()).getJSONObject("root").getString("StoreId")

            for (i in 0 until itemsJson.length()) {
                //itemsJson.put(JsonObject().addProperty("storeId",storeJson))
                itemsJson.getJSONObject(i).put("storeId", storeJson)
            }


            db.FullItemTableDao().upsertTableItem(
                Gson().fromJson(
                    itemsJson.toString(),
                    TypeToken.getParameterized(List::class.java, Item::class.java).type
                )
            )


        }

    }


    fun getItemFromTable(item: String, id: Int){

        viewModelScope.launch(Dispatchers.IO) {
//            mList.postValue(db.shufersalFullTableDao().getAllAlikeNames(item))
            itemOptionList.postValue(db.FullItemTableDao().getAllAlikeNamesFromSuper(item, id))
        }

    }


    fun createSuperItemsTable(superId: Int, brand: BrandToId){
        if(_downloadAndCreateSuperTableProcess.value == false){
            viewModelScope.launch(Dispatchers.Main){
                _downloadAndCreateSuperTableProcess.value=true
            }.invokeOnCompletion {
                viewModelScope.launch(Dispatchers.IO) {
                    val TAG = "singleSearchProductViewModel"
                    Log.d(TAG, "db is empty")

                    val link = getNewLink(superId,py,brand)
                    Log.d(TAG, "link: "+link)
                    val callbackObject: PyObject = py
                        .getModule("python_service_gz_to_json")
                        .callAttr("download",link)

                    if(brand == BrandToId.SHUFERSAL){
                        val itemsJson = JSONObject(callbackObject.toString())
                            .getJSONObject("root")
                            .getJSONObject("Items")
                            .getJSONArray("Item")
                        jsonToSQLTable(itemsJson, superId, brand.brandId)
                    }
                    else{
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

                    //


                }.invokeOnCompletion {
                    viewModelScope.launch(Dispatchers.Main) {
                        _downloadAndCreateSuperTableProcess.value=false
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
        }
        db.FullItemTableDao().upsertTableItem(
            Gson().fromJson(
                itemsJson.toString(),
                TypeToken.getParameterized(List::class.java, Item::class.java).type
            )
        )
    }

    private fun getNewLink(superId: Int, py: Python, brand: BrandToId):String {
        Log.d("getNewLink:", " store id:"+superId)
        if(brand == BrandToId.SHUFERSAL){
            return py.getModule("findSuperLinkByIdDirect")
                        .callAttr("finder",brand.storeIdBaseURL,superId).toString()

        }else{
            return py.getModule("find_vic_super_by_id")
                        .callAttr("getLinkByID",brand.priceBaseURL,superId).toString()
        }

    }

    fun getAllShufersalSupers() {
        viewModelScope.launch(Dispatchers.Main){
            dashboardLoading.value=true
        }

        viewModelScope.launch(Dispatchers.IO) {
            if(db.superTableOfIdAndName().getAllSupers().isEmpty()){
                //shufersal
                val TAG = "MainActivityViewModel"
                Log.d(TAG, "db is empty ")
                val pyShufersalIdAndName = py.getModule("get_shufersal_name_and_id_json").callAttr("starter",BrandToId.SHUFERSAL.priceBaseURL)
                val jsonarray = JSONArray(pyShufersalIdAndName.toString())
                for(i in 0 until jsonarray.length()){
                    jsonarray.getJSONObject(i).put("brand",BrandToId.SHUFERSAL.brandId)
                }
                db.superTableOfIdAndName().upsertTable(Gson().fromJson(jsonarray.toString(),
                    TypeToken.getParameterized(List::class.java, IdToSuperName::class.java).type))
//               victory
                val pyVictoryBrandsIdAndName = py.getModule("victory_scrapping").callAttr("starter",BrandToId.VICTORY.priceBaseURL)
                val jsonArrayOfVictoryItems = JSONArray(pyVictoryBrandsIdAndName.toString())
                db.superTableOfIdAndName().upsertTable(Gson().fromJson(jsonArrayOfVictoryItems.toString(),TypeToken.getParameterized(List::class.java,IdToSuperName::class.java).type))

            }

            }.invokeOnCompletion {
            viewModelScope.launch (Dispatchers.IO){
                superNameAndId.postValue(db.superTableOfIdAndName().getAllSupers())
            }
            viewModelScope.launch(Dispatchers.Main) {
                dashboardLoading.value=false
            }


        }


    }






}