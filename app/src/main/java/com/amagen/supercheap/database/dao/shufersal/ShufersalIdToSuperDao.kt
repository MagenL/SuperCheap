package com.amagen.supercheap.database.dao.shufersal

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.amagen.supercheap.models.*
import java.util.*


const val ID_TO_SUPER_NAME="IdToSuperName"
const val MY_FAV_SUPERS="UserFavouriteSupers"

@Dao
interface ShufersalIdToSuperDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTable(shufersalIdToTable: List<IdToSuperName>)
    @Query("SELECT * FROM $ID_TO_SUPER_NAME")
    suspend fun getShufersalTable(): List<IdToSuperName>

    @Query("select superLink from $ID_TO_SUPER_NAME where storeId=:id ")
    suspend fun getSuperLink(id:Int):String


    @Query("select superName from IdToSuperName where superName like '%'|| :name || '%' ")
    suspend fun getShufersalSuperName(name:String):List<String>

//    @Query("select * from  IdToSuperName where superName= :mySuper")
//    suspend fun getSpecificSuperByName(mySuper:String):UserFavouriteSupers


    @Query("select superName from IdToSuperName")
    suspend fun getAllShufersalSupersName():List<String>

    @Query("DELETE from IdToSuperName")
    suspend fun onDestroyDeleteTable()

//user table insert


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertUserSingleSuper(userFavouriteSupers:UserFavouriteSupers)

//    @Query("select superLink from UserFavouriteSupers where superName like '%'|| :name || '%' ")
//    suspend fun getResultForUserTypingFavSuper(name:String):List<UserFavouriteSupers>

    @Query("select * from idtosupername")
    suspend fun getAllSupers():List<IdToSuperName>


    @Query("select * from UserFavouriteSupers")
    suspend fun getAllUserFavSupers():List<UserFavouriteSupers>



    @Query("select storeId from UserFavouriteSupers where superName = :supername")
    fun getSuperIDbyName(supername:String):Int

    @Query("update userfavouritesupers set date = :date where storeId = :idOfSuper and brand=:brandid")
    fun updateDateOfItemsDB(date: Long, idOfSuper:Int, brandid: Int)

    @Query("select date from userfavouritesupers")
    fun getLastUpdate():Long

    @Query("select superName from userfavouritesupers where storeId = :storeid and brand = :brandid")
    fun getStoreNameByBrandAndStoreId(storeid: Int,brandid:Int):String

    @Query("select *from userfavouritesupers where storeId=:storeid and brand=:brandid")
    fun getUserFavSuperById(storeid: Int, brandid: Int):UserFavouriteSupers

    @Query("delete from userfavouritesupers where storeId=:storeid and brand=:brandid")
    fun deleteSuperFromFavSupers(storeid: Int,brandid: Int)


    @Query("select date from userfavouritesupers where storeId=:storeid and brand=:brandid")
    fun getLastUpdate(storeid: Int,brandid: Int):Long

    @Query("select * from userfavouritesupers where storeId=:storeid and brand=:brandid")
    fun getFavSuperDetail(storeid: Int,brandid: Int):UserFavouriteSupers


}