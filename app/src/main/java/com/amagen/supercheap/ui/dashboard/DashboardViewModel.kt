package com.amagen.supercheap.ui.dashboard

import android.app.Application
import androidx.lifecycle.*
import com.amagen.supercheap.database.ApplicationDB
import com.amagen.supercheap.models.UserFavouriteSupers
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDateTime
import java.util.*

class DashboardViewModel(application: Application) : AndroidViewModel(application) {



    private val _localDateTime= MutableLiveData<LocalDateTime>()
    val localDateTime get() = _localDateTime

    var updateTime:Long=0

    fun getLastUpdate(db: ApplicationDB,storeId: Int,brandId: Int){
        viewModelScope.launch (Dispatchers.IO){
            updateTime = db.superTableOfIdAndName().getLastUpdate(storeId,brandId)
            val triggerTime: LocalDateTime = LocalDateTime.ofInstant(
                Instant.ofEpochMilli(updateTime),
                TimeZone.getDefault().toZoneId()
            )
            _localDateTime.postValue(triggerTime)
        }
    }




    fun deleteTableIdAndName(db:ApplicationDB){
        viewModelScope.launch (Dispatchers.IO){
            db.superTableOfIdAndName().onDestroyDeleteTable()
        }

    }

    fun deleteSuperTable(db:ApplicationDB,storeId:Int, brandId:Int){
        println("storeid =  $storeId brandid = $brandId")
        viewModelScope.launch (Dispatchers.IO){
            db.superTableOfIdAndName().deleteSuperFromFavSupers(storeId,brandId)
            db.FullItemTableDao().deleteSuperTable(storeId,brandId)
        }.invokeOnCompletion {
            println("done")
        }
    }

    fun findSuperAndSetToUserTable(superToAdd: UserFavouriteSupers, db: ApplicationDB){
        viewModelScope.launch(Dispatchers.IO) {
            db.superTableOfIdAndName().upsertUserSingleSuper(superToAdd)

        }
    }
}