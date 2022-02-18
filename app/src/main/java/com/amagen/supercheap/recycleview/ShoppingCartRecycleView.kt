package com.amagen.supercheap.recycleview

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amagen.supercheap.R
import com.amagen.supercheap.databinding.CartDetailsLayoutBinding
import com.amagen.supercheap.models.BrandToId

import com.amagen.supercheap.models.Cart
import com.squareup.picasso.Picasso


class ShoppingCartRecycleView(
    private val carts: List<Cart>
    , private val listener:OnCartClickedListener
    )
    : RecyclerView.Adapter<ShoppingCartRecycleView.ViewHolder>(){
    lateinit var context: Context
    inner class ViewHolder(val binding: CartDetailsLayoutBinding):RecyclerView.ViewHolder(binding.root){
        init {
            binding.root.setOnClickListener {
                if(adapterPosition != RecyclerView.NO_POSITION)
                    listener.CartClicked(carts[adapterPosition])

            }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ShoppingCartRecycleView.ViewHolder {
        context=parent.context
        return ViewHolder(
            CartDetailsLayoutBinding
                .inflate(
                    LayoutInflater
                        .from(parent.context),parent,false)
        )
    }

    override fun onBindViewHolder(holder: ShoppingCartRecycleView.ViewHolder, position: Int) {
        val cart = carts[position]


        val link:String? = findSuperBrandPicture(cart.brandandstoreToprice.storeId_To_BrandId.brandId)
        if(link != null){
            Picasso.get().load(link).into(holder.binding.superPic)
        }
        holder.binding.tvDate.text = context.resources.getString(R.string.cart_created_at_s, cart.date.split("GMT")[0])
        holder.binding.tvPrice.text= context.resources.getString(R.string.total_2f, cart.brandandstoreToprice.price)
        holder.binding.tvSuper.text = context.resources.getString(R.string.super_name_s, cart.brandandstoreToprice.superName)
        holder.binding.tvUploader.text = context.resources.getString(R.string.cart_created_by_s,cart.uploader)

    }

    private fun findSuperBrandPicture(brandId: Int): String? {
        BrandToId.values().iterator().forEach {
            if(it.brandId == brandId){
                return it.iconURL
            }
        }
        return null
    }

    override fun getItemCount() = carts.size


    interface OnCartClickedListener {
        fun CartClicked(cart: Cart)
    }

}