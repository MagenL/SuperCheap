package com.amagen.supercheap.models

const val ITEMS_TABLE = "ITEMS_TABLE"

data class StoreId_To_BrandId(
    val storeId: Int,
    val brandId: Int,
)

data class BrandAndStore_toPrice(
    val storeId_To_BrandId:StoreId_To_BrandId,
    val superName:String?="",
    var price:Double = 0.0,
)

data class BrandAndStore_items(
    val storeId_To_BrandId:StoreId_To_BrandId,
    val superName:String?="",
    val items:List<Item>
)
