package com.amagen.supercheap.recycleview

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.res.stringResource
import androidx.recyclerview.widget.RecyclerView
import com.amagen.supercheap.R
import com.amagen.supercheap.databinding.SuperHistoryBinding
import com.amagen.supercheap.models.Item

class SingleProductRecycleView(
    private val products:List<Item>,
    private val listener: OnItemClickListener
    ):
    RecyclerView.Adapter<SingleProductRecycleView.ViewHolder>() {

    lateinit var context: Context

    inner class ViewHolder(val binding:SuperHistoryBinding) :RecyclerView.ViewHolder(binding.root){
        init {
            binding.root.setOnClickListener {
                if(adapterPosition!=RecyclerView.NO_POSITION){
                    listener.onItemRootClick(products[adapterPosition],adapterPosition)
                }
            }

            binding.root.setOnLongClickListener {
                if (adapterPosition!=RecyclerView.NO_POSITION)
                    listener.onItemRootLongClick(products[adapterPosition],adapterPosition)
                true
            }



        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context=parent.context
        return ViewHolder(SuperHistoryBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product=products[position]
        holder.binding.tvTitleCard.text=product.itemName
        holder.binding.tvDetailCard.text = context.resources.getString(R.string.item_price,product.itemPrice)
    }

    override fun getItemCount()=products.size

    interface OnItemClickListener{
        fun onItemRootClick(item: Item, adapterPosition: Int)
        fun onItemRootLongClick(item:Item,position: Int)
    }
}