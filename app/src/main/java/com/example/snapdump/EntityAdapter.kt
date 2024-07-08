package com.example.snapdump

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.snapdump.databinding.ItemEntityBinding

class EntityAdapter (
    private val entities: List<Entity>,
    private val onItemClick: (Entity) -> Unit
) : RecyclerView.Adapter<EntityAdapter.EntityViewHolder>() {
    inner class EntityViewHolder(private val binding: ItemEntityBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(entity: Entity) {
            binding.title.text = entity.title
            binding.lat.text = entity.lat.toString()
            binding.lon.text = entity.lon.toString()
            val imageURL = "https://labs.anontech.info/cse489/t3/"+entity.image
            Glide.with(binding.image.context)
                .load(imageURL)
                .into(binding.image)

            itemView.setOnClickListener { onItemClick(entity) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntityViewHolder {
        val binding = ItemEntityBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return EntityViewHolder(binding)
    }

    override fun onBindViewHolder(holder: EntityViewHolder, position: Int) {
        holder.bind(entities[position])
    }

    override fun getItemCount() = entities.size

}