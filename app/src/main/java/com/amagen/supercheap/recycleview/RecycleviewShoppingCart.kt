package com.amagen.supercheap.recycleview

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amagen.supercheap.databinding.SuperHistoryBinding
import com.amagen.supercheap.models.Cart

class RecycleviewShoppingCart(private val carts: List<Cart>, private val listener:OnCartClickedListener)
    :RecyclerView.Adapter<RecycleviewShoppingCart.ViewHolder>(){
    lateinit var context: Context
    inner class ViewHolder(val binding:SuperHistoryBinding):RecyclerView.ViewHolder(binding.root){
        init {
//            binding.root.setOnClickListener {
//                if(adapterPosition != RecyclerView.NO_POSITION)
//                    listener.CartClicked(carts[adapterPosition])
//
//            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecycleviewShoppingCart.ViewHolder {
        context=parent.context
        return ViewHolder(
            SuperHistoryBinding
                .inflate(
                    LayoutInflater
                        .from(parent.context),parent,false)
        )
    }

    override fun onBindViewHolder(holder: RecycleviewShoppingCart.ViewHolder, position: Int) {
        val cart = carts[position]
        holder.binding.tvTitleCard.text = cart.superName.toString()
        holder.binding.tvDetailCard.text = cart.totalPrice.toString()
    }

    override fun getItemCount() = carts.size


    interface OnCartClickedListener {
        fun CartClicked(cart: Cart)
    }

}