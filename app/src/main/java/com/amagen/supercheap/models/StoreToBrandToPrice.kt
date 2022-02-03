package com.amagen.supercheap.models

const val SUB_TABLE_NAME_ITEMS_ITEM = "ShufersalTableItemItem"

data class StoreId_To_BrandId(
    val storeId: Int,
    val brandId: Int,
)

data class BrandAndStore_toPrice(
    val storeId_To_BrandId:StoreId_To_BrandId,
    val superName:String?="",
    var price:Double = 0.0,
)

