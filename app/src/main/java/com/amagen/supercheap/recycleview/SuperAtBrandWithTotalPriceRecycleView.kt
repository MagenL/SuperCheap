package com.amagen.supercheap.recycleview

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.amagen.supercheap.R
import com.amagen.supercheap.databinding.SuperAtBrandWithTotalPriceBinding
import com.amagen.supercheap.models.BrandAndStore_toPrice
import com.amagen.supercheap.models.BrandToId
import com.amagen.supercheap.models.Item
import com.squareup.picasso.Picasso

class SuperAtBrandWithTotalPriceRecycleView(
    private val supersWithPrice: List<BrandAndStore_toPrice>,
    private val listener: OnSuperClickListener,
    private val items:List<Item>
):RecyclerView.Adapter<SuperAtBrandWithTotalPriceRecycleView.ViewHolder>(){

    lateinit var context: Context

    inner class ViewHolder(val binding:SuperAtBrandWithTotalPriceBinding):RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                if(adapterPosition!=RecyclerView.NO_POSITION){
                    listener.onSuperRootClick(supersWithPrice[adapterPosition],items)
                }
            }
        }
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        context = parent.context
        return ViewHolder(SuperAtBrandWithTotalPriceBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val superWithTotalprice = supersWithPrice[position]
        val superDetails= BrandToId.values().find { it.brandId==superWithTotalprice.storeId_To_BrandId.brandId }
        holder.binding.tvSuperName.text = superWithTotalprice.superName
        holder.binding.tvTotalBill.text=context.resources.getString(R.string.total_bill,superWithTotalprice.price)
        Picasso.get().load(superDetails!!.iconURL).noFade().fit().into(holder.binding.ivSuper)
    }

    override fun getItemCount()= supersWithPrice.size

    interface OnSuperClickListener{
        fun onSuperRootClick(superWithPrice: BrandAndStore_toPrice,items: List<Item>)
    }

}

