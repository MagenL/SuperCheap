package com.amagen.supercheap.ui.onlineCarts

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amagen.supercheap.models.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue

class OnlineCartsViewModel : ViewModel(){
    private val TAG = "HistoryViewModel"
    private val mAuth = FirebaseAuth.getInstance()
    private val cartHistoryReference= FirebaseDatabase.getInstance()
        .reference
        .child("Carts")
    private val userHistoryReference = FirebaseDatabase.getInstance().reference.child("users").child(mAuth.currentUser!!.uid).child("Carts")


    private val _carts = MutableLiveData<List<Cart>>()
    val carts get() = _carts

    private val cartList = ArrayList<Cart>()

    private val _uploaderCart = MutableLiveData<List<Cart>>()
    val uploaderCart get() = _uploaderCart

    private val _uploaderNames = MutableLiveData<List<String>>()
    val uploaderNames get() = _uploaderNames



    init {
        userHistoryReference.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    snapshot.children.forEach { it ->
                        val cartId = it.getValue(Long::class.java)
                        if (cartId != null) {
                            cartHistoryReference.child(cartId.toString()).addListenerForSingleValueEvent(object:ValueEventListener{
                                override fun onDataChange(snapshot: DataSnapshot) {
                                    if(snapshot.exists()){
                                        val cart = getCartIfSnaphotExists(snapshot)
                                        cartList.add(cart)
                                        _carts.postValue(cartList)
                                    }
                                }
                                override fun onCancelled(error: DatabaseError) {
                                    println(error)
                                }

                            })
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })



    }

    private fun getCartIfSnaphotExists(snapshot:DataSnapshot): Cart {
        println(snapshot.ref)
        val base = snapshot.child("brandandstoreToprice")
        val storeIdToBrandId = StoreId_To_BrandId(
            base.child("storeId_To_BrandId").child("storeId").getValue(Int::class.java)!!,
            base.child("storeId_To_BrandId").child("brandId").getValue(Int::class.java)!!
        )
        val brandAndStore_toPrice = BrandAndStore_toPrice(
            storeIdToBrandId,
            base.child("superName").getValue(String::class.java),
            base.child("price").getValue(Double::class.java)!!
        )
        val date = snapshot.child("date").getValue<String>()?:""
        val uploader = snapshot.child("uploader").getValue<String>()?:""
        val iterItems = snapshot.child("items").children
        val items = ArrayList<Item>()
        for (item in iterItems) {
            items.add(
                Item(
                    item.child("priceUpdateDate").getValue<String>()!!,
                    item.child("itemCode").getValue<Long>()!!,
                    item.child("itemType").getValue<Int>(),
                    item.child("itemName").getValue<String>()!!,
                    item.child("manufacturerName").getValue<String>(),
                    item.child("manufacturerCountry").getValue<String>(),
                    item.child("unitQuantity").getValue<String>(),
                    item.child("quantity").getValue<Double>(),
                    item.child("bisWeighted").getValue<Double>(),
                    item.child("unitOfMeasure").getValue<String>(),
                    item.child("quantityInPackage").getValue<Int>(),
                    item.child("itemPrice").getValue<Double>()!!,
                    item.child("unitOfMeasurePrice").getValue<Double>(),
                    item.child("itemStatus").getValue<Int>(),
                    item.child("storeId").getValue<Int>()!!,
                    item.child("brandId").getValue<Int>()!!,
                )
            )
        }
        val uploadTime = snapshot.child("uploadTime").getValue<Long>()!!
        return Cart(date,brandAndStore_toPrice,uploader,items,uploadTime)
    }

    fun getAllUploaderByName(){
        val uploaders = ArrayList<String>()
        cartHistoryReference.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.iterator().forEach {
                    uploaders.add(it.child("uploader").getValue<String>()!!)
                    _uploaderNames.postValue(uploaders)
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    fun getCartByUploader(uploader:String){
        val cart = ArrayList<Cart>()
        cartHistoryReference.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                snapshot.children.iterator().forEach {
                    if(it.child("uploader").getValue<String>()!! == uploader){
                        cart.add(getCartIfSnaphotExists(it))
                    }
                    _uploaderCart.postValue(cart)
                }
            }
            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    fun attachCartToProfile(cart: Cart){
        userHistoryReference.addListenerForSingleValueEvent(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    var isExist = false
                    snapshot.children.forEach {
                        if(it.getValue<Long>() == cart.uploadTime){
                            isExist=true
                            return@forEach
                        }
                    }
                    if(!isExist){
                        userHistoryReference.push().setValue(cart.uploadTime).addOnFailureListener {

                        }
                    }

                }



            }

            override fun onCancelled(error: DatabaseError) {

            }

        })


    }

}