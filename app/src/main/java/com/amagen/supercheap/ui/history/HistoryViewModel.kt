package com.amagen.supercheap.ui.history

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.amagen.supercheap.models.Cart
import com.amagen.supercheap.models.Item
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HistoryViewModel : ViewModel(){
    private val TAG = "HistoryViewModel"
    private val mAuth = FirebaseAuth.getInstance()
    private val cartHistoryReference= FirebaseDatabase.getInstance()
        .reference
        .child("users")
        .child(mAuth.currentUser?.uid.toString())
        .child("shopping")

    private val tempCart = ArrayList<Cart>()
    private val tempProducts = ArrayList<Item>()
    private val carts = MutableLiveData<List<Cart>>()
    private val productsInCart= MutableLiveData<List<Item>>()
    val userCart get() = carts
    val userProductsInCart get() = productsInCart

    init {
        Log.d(TAG, ": "+cartHistoryReference.ref)
        cartHistoryReference.addListenerForSingleValueEvent(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    Log.d(TAG, "atleast 1 cart found")
                    snapshot.children.iterator().forEach {
                    //cart
                        var totalPrice:Double=0.0

                        it.children.iterator().forEach {
                          //products
                            if(it.child("itemPrice").getValue(Int::class.java)!=null){
                                totalPrice+= it.child("itemPrice").getValue(Double::class.java)!!
                            }
                        }
                        var storeId:Int = it.child("0").child("storeId").getValue(Int::class.java) as Int
                        tempCart.add(Cart(totalPrice,storeId))

                    }
                    carts.postValue(tempCart)
                }
                else{
                    Log.d(TAG, "cart is empty ")
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

}