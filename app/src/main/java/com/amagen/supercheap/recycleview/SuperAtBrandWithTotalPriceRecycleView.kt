package com.amagen.supercheap.recycleview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.amagen.supercheap.databinding.SuperAtBrandWithTotalPriceBinding
import com.amagen.supercheap.models.BrandAndStore_toPrice
import com.amagen.supercheap.models.BrandToId
import com.amagen.supercheap.models.Item
import com.squareup.picasso.Picasso

class SuperAtBrandWithTotalPriceRecycleView(
    private val supersWithPrice: List<BrandAndStore_toPrice>,
    private val listener: OnSuperClickListener,
    private val myParentFragmentManger: FragmentManager?,
    private val items:List<Item>
):RecyclerView.Adapter<SuperAtBrandWithTotalPriceRecycleView.ViewHolder>(){


    inner class ViewHolder(val binding:SuperAtBrandWithTotalPriceBinding):RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if(adapterPosition!=RecyclerView.NO_POSITION){
                    listener.onSuperRootClick(supersWithPrice[adapterPosition],myParentFragmentManger,items)
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(SuperAtBrandWithTotalPriceBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val superWithTotalprice = supersWithPrice[position]
        val superDetails= BrandToId.values().find { it.brandId==superWithTotalprice.storeId_To_BrandId.brandId }

        holder.binding.tvSuperName.text = superWithTotalprice.superName
        holder.binding.tvTotalBill.text= superWithTotalprice.price.toString()
        Picasso.get().load(superDetails!!.iconURL).noFade().fit().into(holder.binding.ivSuper)
    }

    override fun getItemCount()= supersWithPrice.size

    interface OnSuperClickListener{
        fun onSuperRootClick(superWithPrice: BrandAndStore_toPrice, myParentFragmentManger: FragmentManager?,items: List<Item>)
    }

}

