package com.amagen.supercheap.webScrapping


import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.amagen.supercheap.models.BrandToId
import com.amagen.supercheap.models.IdToSuperName
import kotlinx.coroutines.*
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.select.Elements
import java.net.SocketTimeoutException
import kotlin.collections.ArrayList


const val base_url = "http://prices.shufersal.co.il/FileObject/UpdateCategory?catID=2&page="
private val shufersal:ArrayList<IdToSuperName> = ArrayList()
private var downloadedpages:Int=0

class ShufersalWebScrapping() {

    private lateinit var doc: Document

    companion object{

        private var _progress : MutableLiveData<Int> = MutableLiveData(0)
        val progress: LiveData<Int> get() = _progress

        suspend fun getData():List<IdToSuperName> {


            return withContext(Dispatchers.IO){
                val firstJob = CoroutineScope(Dispatchers.IO).launch{
                    ShufersalWebScrapping().getShufersalTable(1,7)
                    println("1 d")
                }
                val secondJob = CoroutineScope(Dispatchers.IO).launch{
                    ShufersalWebScrapping().getShufersalTable(7,15)
                    println("2 d")
                }
                val thirdJob = CoroutineScope(Dispatchers.IO).launch{
                    ShufersalWebScrapping().getShufersalTable(15,23)
                    println("3 d")
                }

                firstJob.start()
                secondJob.start()
                thirdJob.start()
                firstJob.join()
                thirdJob.join()
                secondJob.join()


                println("returning data")
                println(shufersal.size)
                return@withContext shufersal
            }



        }

    }





    private fun getShufersalTable(from:Int, to:Int) {
        println("started")
        for (i in from until to) {
            try {
                getSinglePage(i)
            }
            catch (e: SocketTimeoutException){
                try {
                    Thread.sleep(500)
                    println("thread "+Thread.currentThread().id+" has been restarted")
                    getSinglePage(i)
                }catch (e:Exception){
                    e.printStackTrace()
                }
            }

        }

    }

    private fun getSinglePage(from: Int) {
        doc = Jsoup.connect(base_url + from).get()
        //Elements elements = doc.getElementsByClass("webgrid-alternating-row").addClass("webgrid-row-style");
        val elements = doc
            .getElementsByClass("webgrid-alternating-row")
        elements.addAll(doc.getElementsByClass("webgrid-row-style"))

        for (element in elements) {
            val td: Elements = element.getElementsByTag("td")

            var name = td[5].text()
            if (name.isNotEmpty()) {

                //                val link = td[0].toString()
                //                    .replace(replace_start, "")
                //                    .replace(replace_end, "")
                //                    .replace(replace_condition_amp, "")

                name = name.toString()
                            .replace("<td>", "")
                            .replace("</td>", "")

                val id: Int = getIdFromName(name)
                shufersal.add(IdToSuperName(id,name,"",BrandToId.SHUFERSAL.brandId))
                downloadedpages++
                _progress.postValue(downloadedpages)

            }
        }
    }

    fun getIdFromName(name: String): Int {
        var isFirst:Boolean=false
        var number = 0
        for(let in name){
            if(let.isDigit()){
                if(isFirst){
                    number = (number*10)+let.digitToInt()

                }else if(!isFirst){
                    number = let.digitToInt()
                    isFirst=true
                }
            }
        }
        return number
    }



}


