package com.amagen.supercheap.models

import androidx.room.Entity
import com.google.gson.annotations.SerializedName


@Entity(tableName = ITEMS_TABLE, primaryKeys = ["itemCode","storeId","brandId","itemName"],)
data class Item(
    @SerializedName("PriceUpdateDate")
    val priceUpdateDate:String?,
    @SerializedName("ItemCode")
    val itemCode:Long,
    @SerializedName("ItemType")
    val itemType:Int?,
    @SerializedName("ItemName")
    val itemName:String,
    @SerializedName("ManufacturerName")
    val manufacturerName:String?,
    @SerializedName("ManufactureCountry")
    val manufacturerCountry:String?,
    @SerializedName("UnitQty")
    val unitQuantity:String?,
    @SerializedName("Quantity")
    val quantity:Double?,
    @SerializedName("bIsWeighted")//might be number or string
    val bIsWeighted:Double?,
    @SerializedName("UnitOfMeasure")
    val unitOfMeasure:String?,
    @SerializedName("QtyInPackage")
    val quantityInPackage:Int?,
    @SerializedName("ItemPrice")
    val itemPrice:Double?,
    @SerializedName("UnitOfMeasurePrice")
    val unitOfMeasurePrice:Double?,
//    @SerializedName("AllowDiscount")
//    val allowDiscount:Int,
    @SerializedName("ItemStatus")
    val itemStatus:Int?,
    var storeId:Int,
    var brandId:Int
)

data class ItemFromFB(
    val itemName:String,
    val manufacturerName:String?,
    val manufacturerCountry:String?,
    val unitQuantity:String?,
    val quantity:Double?,
    val unitOfMeasure:String?,
    val itemPrice:Double,
    val unitOfMeasurePrice:Double?,
)


