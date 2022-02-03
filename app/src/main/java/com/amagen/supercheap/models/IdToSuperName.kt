package com.amagen.supercheap.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity
data class IdToSuperName (
    @PrimaryKey
    @SerializedName("id")
    val storeId:Int,
    @SerializedName("name")
    var superName:String,
    @SerializedName("link")
    val superLink:String,
    val brand:Int = BrandToId.SHUFERSAL.brandId
)