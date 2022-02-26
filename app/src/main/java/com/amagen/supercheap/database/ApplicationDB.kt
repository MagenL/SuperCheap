package com.amagen.supercheap.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.amagen.supercheap.database.dao.shufersal.ShufersalDao
import com.amagen.supercheap.database.dao.shufersal.ShufersalIdToSuperDao
import com.amagen.supercheap.models.IdToSuperName
import com.amagen.supercheap.models.Item
import com.amagen.supercheap.models.UserFavouriteSupers


const val DB_VERSION=2
const val DB_NAME="ApplicationDB"
@Database(entities = [Item::class, IdToSuperName::class, UserFavouriteSupers::class], version = DB_VERSION)
abstract class ApplicationDB:RoomDatabase() {

    companion object{
        fun create(context:Context):ApplicationDB=Room.databaseBuilder(context, ApplicationDB::class.java, DB_NAME)
            .fallbackToDestructiveMigration().build()
    }
    //get the dao object.
    abstract fun FullItemTableDao(): ShufersalDao
    abstract fun superTableOfIdAndName(): ShufersalIdToSuperDao


}