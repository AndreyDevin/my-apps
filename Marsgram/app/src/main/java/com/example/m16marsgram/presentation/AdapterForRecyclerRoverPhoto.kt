package com.example.m16marsgram.presentation

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.m16marsgram.data.MarsPhotoDto
import com.example.m16marsgram.databinding.CardPhotoBinding

class RoverPhotoViewHolder(val binding: CardPhotoBinding) : RecyclerView.ViewHolder(binding.root)

class DiffUtilCallback : DiffUtil.ItemCallback<MarsPhotoDto>() {
    override fun areItemsTheSame(oldItem: MarsPhotoDto, newItem: MarsPhotoDto): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: MarsPhotoDto, newItem: MarsPhotoDto): Boolean =
        oldItem == newItem
}

class RoverPhotoRecyclerAdapter(val listener: (String) -> Unit) :
    PagingDataAdapter<MarsPhotoDto, RoverPhotoViewHolder>(DiffUtilCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RoverPhotoViewHolder {
        return RoverPhotoViewHolder(
            CardPhotoBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }
    override fun onBindViewHolder(holder: RoverPhotoViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.binding) {
            item?.let { marsPhotoDto ->
                textInCardStartTop.text = marsPhotoDto.rover.name
                textInCardStartBottom.text = marsPhotoDto.camera.name
                "Sol: ${marsPhotoDto.sol}".also { textInCardEndTop.text = it }
                textInCardEndBottom.text = marsPhotoDto.earthDate
                cardView.setOnClickListener {
                    listener(marsPhotoDto.imgSrc)
                }
                Glide
                    .with(imageView.context)
                    .load(marsPhotoDto.imgSrc)
                    .into(imageView)
            }
        }
    }
}