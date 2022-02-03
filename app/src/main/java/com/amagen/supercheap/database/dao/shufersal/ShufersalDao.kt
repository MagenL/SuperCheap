package com.amagen.supercheap.database.dao.shufersal

import androidx.lifecycle.LiveData
import androidx.room.*
import com.amagen.supercheap.models.*
import kotlinx.coroutines.selects.select

@Dao
interface ShufersalDao {
    //replace by the primary key- check chainId or storeId

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTableItem(shufersalDbAsItems: List<Item>)

    @Query("SELECT * FROM $SUB_TABLE_NAME_ITEMS_ITEM")
    suspend fun getShufersalTable():List<Item>

    @Query("SELECT * FROM $SUB_TABLE_NAME_ITEMS_ITEM where storeId = :storeid")
    fun getShufersalTableById(storeid: Int):List<Item>


    @Query("SELECT * FROM $SUB_TABLE_NAME_ITEMS_ITEM WHERE itemName LIKE '%'||:value ||'%'")
    suspend fun getAllAlikeNames(value:String):List<Item>

    @Query("select * from $SUB_TABLE_NAME_ITEMS_ITEM where itemName LIKE '%'|| :itemname ||'%' AND storeId = :storeid")
    suspend fun getAllAlikeNamesFromSuper(itemname:String, storeid:Int):List<Item>

    @Query("delete from $SUB_TABLE_NAME_ITEMS_ITEM where storeId =:superid and brandId=:brandid")
    fun deleteSuperTable(superid:Int,brandid: Int)
//    @Query("select itemName FROM $SUB_TABLE_NAME_ITEMS_ITEM")
//    suspend fun getAllProductNames():List<Item>

    @Query("select * from $SUB_TABLE_NAME_ITEMS_ITEM GROUP BY itemName HAVING COUNT(itemName)>1 order by itemPrice asc" )
    suspend fun countItemNames():List<Item>

    @Query("select distinct storeId, brandId from $SUB_TABLE_NAME_ITEMS_ITEM GROUP BY itemName HAVING COUNT(itemName)>1 " )
    suspend fun getSuperAndBrandForFoundItems():List<StoreId_To_BrandId>


    //find all products which repeats in all supers - >
//    @Query("select distinct storeId, brandId from $SUB_TABLE_NAME_ITEMS_ITEM GROUP BY itemName HAVING COUNT(itemName)=:allUserSupers " )
//    suspend fun getSuperAndBrandForFoundItems(allUserSupers:Int):List<StoreId_To_BrandId>


    @Query("select distinct itemPrice from $SUB_TABLE_NAME_ITEMS_ITEM where storeId = :storeid and brandId = :brandid and itemName= :itemname")
    fun getPriceFromSuper(storeid: Int, brandid:Int, itemname: String):Double

    @Query("select brandId, storeId from $SUB_TABLE_NAME_ITEMS_ITEM where itemName = :itemname")
    suspend fun findAllSupersWithUserListItems(itemname:String):List<StoreId_To_BrandId>


    @Query("select * from shufersaltableitemitem where storeId=:storeid and brandId=:brandid and itemName=:itemname")
    fun getItemFromStoreAtBrand(storeid: Int, brandid: Int, itemname: String):Item



//when adding a new table run these 3 queries to avoid duplicated rows.
    @Query("select distinct * from shufersaltableitemitem where storeId=:storeid group by itemName having count(itemName)>1")
    fun getAllDuplicateRows(storeid: Int):List<Item>

    @Query("delete from shufersaltableitemitem  where itemName in (select itemName from shufersaltableitemitem where  storeId=:storeid  group by itemName having count(itemName) > 1 )")
    fun deleteAllDuplicateRows(storeid: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeletedDuplicatedRows(dupItems: List<Item>)
//end

}