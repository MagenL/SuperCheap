package com.amagen.supercheap.ui.home.searchproducts.bysingle

import android.app.Application
import androidx.lifecycle.*
import com.amagen.supercheap.database.ApplicationDB
import com.amagen.supercheap.models.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class SingleSearchProductViewModel(application:Application) : AndroidViewModel(application) {

    private var linkToSuper = MutableLiveData<List<UserFavouriteSupers>>()
    val supersLink:LiveData<List<UserFavouriteSupers>>  get() =  linkToSuper

    private var itemName = MutableLiveData<List<Item>>()
    val itemFromSuper get() = itemName

    private var loadingSingleSearchMLD = MutableLiveData(false)
    val loadingFragment get() = loadingSingleSearchMLD


    private var _mAuth:FirebaseAuth? = null
    private val mAuth get() = _mAuth!!

    private var _uploadToFireBaseListener=MutableLiveData(false)
    val uploadToFirebaseListener get() = _uploadToFireBaseListener


    init {
        _mAuth = FirebaseAuth.getInstance()
    }

    fun getAllUserSupers(db: ApplicationDB){
        viewModelScope.launch(Dispatchers.IO) {
            linkToSuper.postValue(db.superTableOfIdAndName().getAllUserFavSupers())
        }
    }
    fun getSuperTableById(superId:Int, superBrand:Int, db: ApplicationDB){
        loadingSingleSearchMLD.postValue(true)
        itemName.postValue(db.FullItemTableDao().getShufersalTableById(superId, superBrand ))
        loadingSingleSearchMLD.postValue(false)
    }

//    if you want to turn it back on, you got to transfer brand id into deletesupertable function.
//    fun deletesOldSuperTable(superId: Int, shufersalTableDao: ShufersalDao){
//        shufersalTableDao.deleteSuperTable(superId)
//    }

    suspend fun getSuperName(db: ApplicationDB,storeIdAndBrandId: StoreId_To_BrandId):String{
        return withContext(Dispatchers.IO){
            db.superTableOfIdAndName().getStoreNameByBrandAndStoreIdUserFavTable(storeIdAndBrandId.storeId,storeIdAndBrandId.brandId)
        }
    }

    fun uploadCartToDB(items:List<Item>,superName:String) {
        val fbCartReference: DatabaseReference = FirebaseDatabase.getInstance().reference.child("Carts")
        val fbUserReference:DatabaseReference=FirebaseDatabase.getInstance().reference.child("users").child(mAuth.currentUser?.uid.toString())
        val uploadTime:Long = Calendar.getInstance().timeInMillis
//        val uploader = Uploader(items,
//            mAuth.currentUser?.displayName?:mAuth.currentUser?.email.toString()
//            ,superName
//        )
        val cart = Cart(Date().toString(),
            BrandAndStore_toPrice(StoreId_To_BrandId(items[0].storeId,items[0].brandId),superName,items.sumOf { it.itemPrice!! }),
            mAuth.currentUser!!.displayName?: mAuth.currentUser!!.email!!,items,uploadTime)

        fbCartReference.child(uploadTime.toString())
            .setValue(cart).addOnCompleteListener {
                if(it.isComplete){
                    fbUserReference.child("Carts").push().setValue(uploadTime).addOnFailureListener {
                        _uploadToFireBaseListener.postValue(false)
                    }
                    _uploadToFireBaseListener.postValue(true)
                }else {
                    _uploadToFireBaseListener.postValue(false)
                }
            }
    }
    fun addSuperToFavorite(mySuper:StoreId_To_BrandId, db:ApplicationDB){
        viewModelScope.launch(Dispatchers.IO) {
            val superLink =db.superTableOfIdAndName().getSuperLink(mySuper.storeId, mySuper.brandId)
            println("superlink$superLink")
            val superName=db.superTableOfIdAndName().getStoreNameByBrandAndStoreIdGeneralTable(mySuper.storeId, mySuper.brandId)
            println("superName:$superName")
            val userFavouriteSupers = UserFavouriteSupers(
                mySuper.storeId,
                superName,
                superLink,
                Calendar.getInstance().timeInMillis,
                mySuper.brandId
             )
             println("to add -$userFavouriteSupers")

                    db.superTableOfIdAndName().upsertUserSingleSuper(userFavouriteSupers)
        }
    }

    suspend fun getSuperName(db: ApplicationDB, storeId:Int, brandId:Int):String{
        return withContext(Dispatchers.IO){
            return@withContext db.superTableOfIdAndName().getStoreNameByBrandAndStoreIdGeneralTable(storeId,brandId)
        }
    }



}