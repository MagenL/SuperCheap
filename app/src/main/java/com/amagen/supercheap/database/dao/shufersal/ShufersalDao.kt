package com.amagen.supercheap.database.dao.shufersal

import androidx.room.*
import com.amagen.supercheap.models.*

@Dao
interface ShufersalDao {
    //replace by the primary key- check chainId or storeId


    //-----------------------init supers table-------------------------//
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertTableItem(superDbAsItems: List<Item>)


    @Query("SELECT * FROM $ITEMS_TABLE where storeId = :storeid and brandId=:brand")
    fun getShufersalTableById(storeid: Int, brand: Int):List<Item>


    //---------------------------query that returns alike names --------------------------------//
    @Query("select * from $ITEMS_TABLE where itemName LIKE '%'|| :itemname ||'%' AND storeId = :storeid")
    suspend fun getAllAlikeNamesFromSuper(itemname:String, storeid:Int):List<Item>

    //----------------------------------deletes super table-------------------------------------//
    @Query("delete from $ITEMS_TABLE where storeId =:superid and brandId=:brandid")
    fun deleteSuperTable(superid:Int,brandid: Int)



    //--------------getting items that matches to user's brand conditions-------------------//
    @Query("select * from $ITEMS_TABLE " +
            "where (:shufersal is 0 or brandId=:shufersal) " +
            "or (:victory is 0 or brandId=:victory) " +
            "or (:hcohen is 0 or brandId=:hcohen)" +
            "or (:mahsaniAshok is 0 or brandId=:mahsaniAshok)" +
            "or (:bareket is 0 or brandId=:bareket)"+
            "GROUP BY itemName HAVING COUNT(itemName)>:times order by itemPrice asc" )
    suspend fun countItemNames(
        times:Int?=1,
        shufersal:Int?=0,
        victory:Int?=0,
        hcohen:Int?=0,
        mahsaniAshok:Int?=0,
        bareket:Int?=0
    ):List<Item>

    @Query("select distinct storeId, brandId from $ITEMS_TABLE GROUP BY itemName HAVING COUNT(itemName)>1 " )
    suspend fun getSuperAndBrandForFoundItems():List<StoreId_To_BrandId>

    @Query("select count(distinct storeId) from $ITEMS_TABLE")
    fun getUserStoresCount():Int


    //find all products which repeats in all supers - >
//    @Query("select distinct storeId, brandId from $SUB_TABLE_NAME_ITEMS_ITEM GROUP BY itemName HAVING COUNT(itemName)=:allUserSupers " )
//    suspend fun getSuperAndBrandForFoundItems(allUserSupers:Int):List<StoreId_To_BrandId>


    @Query("select distinct itemPrice from $ITEMS_TABLE where storeId = :storeid and brandId = :brandid and itemName= :itemname")
    fun getPriceFromSuper(storeid: Int, brandid:Int, itemname: String):Double


    //--------------getting stores at brands terms to user's super conditions-------------------//
    @Query("select brandId, storeId from $ITEMS_TABLE where itemName = :itemname and " +
            "(:shufersal is null or brandId=:shufersal) " +
            "or (:victory is null or brandId=:victory) " +
            "or (:hcohen is null or brandId=:hcohen)" +
            "or (:mahsaniAshok is null or brandId=:mahsaniAshok)" +
            "or (:bareket is null or brandId=:bareket)"+
            "")
    suspend fun findAllSupersWithUserListItems(itemname:String,
                                               shufersal:Int?=null,
                                               victory:Int?=null,
                                               hcohen:Int?=null,
                                               mahsaniAshok:Int?=null,
                                               bareket:Int?=null
    ):List<StoreId_To_BrandId>


    @Query("select * from $ITEMS_TABLE where storeId=:storeid and brandId=:brandid and itemName=:itemname")
    fun getItemFromStoreAtBrand(storeid: Int, brandid: Int, itemname: String):Item

    @Query("select * from $ITEMS_TABLE where :condition")
    fun getItemsWithFilter(condition:String):List<Item>



//when adding a new table run these 3 queries to avoid duplicated rows.
    @Query("select distinct * from $ITEMS_TABLE where storeId=:storeid group by itemName having count(itemName)>1")
    fun getAllDuplicateRows(storeid: Int):List<Item>

    @Query("delete from $ITEMS_TABLE  where itemName in (select itemName from $ITEMS_TABLE where  storeId=:storeid  group by itemName having count(itemName) > 1 )")
    fun deleteAllDuplicateRows(storeid: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDeletedDuplicatedRows(dupItems: List<Item>)
//end

}