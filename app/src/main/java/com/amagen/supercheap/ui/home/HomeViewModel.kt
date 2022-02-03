package com.amagen.supercheap.ui.home

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.amagen.supercheap.database.ApplicationDB
import com.amagen.supercheap.models.IdToSuperName
import com.amagen.supercheap.models.Item
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject

class HomeViewModel(application: Application) : AndroidViewModel(application) {


//    private val mList = MutableLiveData<List<Item>>()
//    val mylist: LiveData<List<Item>> = mList
//
//
//    init{
//        val db = ApplicationDB.create(application)
//
//
//        viewModelScope.launch(Dispatchers.Main) {
//
//
//            val callbackObject:PyObject = py.getModule("python_service_gz_to_json").callAttr("download",url)
//            val itemsJson = JSONObject(callbackObject.toString()).getJSONObject("root").getJSONObject("Items").getJSONArray("Item")
//            val storeJson = JSONObject(callbackObject.toString()).getJSONObject("root").getString("StoreId")
//            var i =0
//            for(i in 0 until  itemsJson.length()){
//                //itemsJson.put(JsonObject().addProperty("storeId",storeJson))
//                itemsJson.getJSONObject(i).put("storeId",storeJson)
//            }
//            val pyShufersalIdAndName = py.getModule("get_shufersal_name_and_id_json").callAttr("starter")
//            val jsonarray = JSONArray(pyShufersalIdAndName.toString())
//
//            viewModelScope.launch (Dispatchers.IO){
//
//                db.shufersalTableOfIdAndName().upsertTable(Gson().fromJson(jsonarray.toString(),TypeToken.getParameterized(List::class.java,IdToSuperName::class.java).type))
//                db.shufersalFullTableDao().upsertTableItem(Gson().fromJson(itemsJson.toString(),TypeToken.getParameterized(List::class.java,Item::class.java).type))
//
//
//                Log.d("mycheck", ": "+db.shufersalFullTableDao().getAllAlikeNames("עגבניה"))
//
//                mList.postValue(db.shufersalFullTableDao().getAllAlikeNames("עגבניה"))
//
//            }
//        }
//
//
//
//    }


}