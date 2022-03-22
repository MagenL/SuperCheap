package com.amagen.supercheap.webScrapping

import com.amagen.supercheap.models.IdToSuperName
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ShufersalWebScrappingRepo {
    companion object{
        suspend fun getShufersalDetails():List<IdToSuperName>{
            return withContext(Dispatchers.IO){
                ShufersalWebScrapping.getData()
            }
        }
    }
}