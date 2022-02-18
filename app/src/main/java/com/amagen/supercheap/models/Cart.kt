package com.amagen.supercheap.models

data class Cart (
    val date:String,
    val brandandstoreToprice: BrandAndStore_toPrice,
    val uploader:String,
    val items:List<Item>,
    val uploadTime:Long
)

