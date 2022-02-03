package com.amagen.supercheap.ui.home.recycleview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.amagen.supercheap.databinding.LayoutElementBinding
import com.amagen.supercheap.models.Elements
import com.squareup.picasso.Picasso

class UserElementsRecycleView(private val elements:List<Elements>, private val listener: OnElementClickListener):
    RecyclerView.Adapter<UserElementsRecycleView.ViewHolder>(){

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(LayoutElementBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val element = elements[position]


        Picasso.get().load(element.brand.iconURL).into(holder.binding.ivElement)
        println(element.brand.iconURL)
        holder.binding.tvElement.text = element.name

    }

    override fun getItemCount()=elements.size


    inner class ViewHolder(val binding:LayoutElementBinding):RecyclerView.ViewHolder(binding.root){
        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION)
                    listener.onElementRootClick(elements[adapterPosition],adapterPosition)
            }
            binding.root.setOnLongClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION)
                    listener.onElementLongClick(elements[adapterPosition],adapterPosition)
                true
            }
        }
    }

    interface OnElementClickListener {
        fun onElementRootClick(elements: Elements, adapterPosition: Int)
        fun onElementLongClick(elements: Elements, adapterPosition: Int)

    }
}