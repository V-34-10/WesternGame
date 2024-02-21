package com.westerngans.tersslots.ui.scene.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.westerngans.tersslots.R
import com.westerngans.tersslots.ui.scene.model.Slot

class SlotMinerAdapter(
    private var defaultList: List<Slot>,
    private val maxOpenSlot: Int
) :
    RecyclerView.Adapter<SlotMinerAdapter.SlotViewHolder>() {
    private var slotMutList: MutableList<Int>? = null
    private var itemSlotClickListener: SlotClickListener? = null
    private var openSlotCount = 0
    private var imageSlot: Int = 0
    private var imgSlot: Int = 0

    init {
        if (maxOpenSlot == 1) {
            slotMutList = mutableListOf(
                R.drawable.ic_slot_3d,
                R.drawable.ic_slot_4d,
                R.drawable.ic_slot_5d,
                R.drawable.ic_slot_6d,
                R.drawable.ic_slot_7d,
                R.drawable.ic_slot_8d,
                R.drawable.ic_slot_2d,
                R.drawable.ic_slot_2d
            )
            imageSlot = R.layout.item_slot
            imgSlot = R.id.imageSlot
        } else {
            slotMutList = mutableListOf(
                R.drawable.ic_slot_2e,
                R.drawable.ic_slot_1e,
                R.drawable.ic_slot_2e,
                R.drawable.ic_slot_3e,
                R.drawable.ic_slot_3e,
                R.drawable.ic_slot_3e,
                R.drawable.ic_slot_2e,
                R.drawable.ic_slot_1e,
                R.drawable.ic_slot_2e,
                R.drawable.ic_slot_1e,
                R.drawable.ic_slot_2e,
                R.drawable.ic_slot_3e,
                R.drawable.ic_slot_3e,
                R.drawable.ic_slot_2e,
                R.drawable.ic_slot_1e,
                R.drawable.ic_slot_2e,
                R.drawable.ic_slot_3e,
                R.drawable.ic_slot_1e,
                R.drawable.ic_slot_2e,
                R.drawable.ic_slot_1e,
                R.drawable.ic_slot_1e,
                R.drawable.ic_slot_2e,
                R.drawable.ic_slot_3e,
                R.drawable.ic_slot_3e,
                R.drawable.ic_slot_2e,
                R.drawable.ic_slot_3e,
                R.drawable.ic_slot_3e,
                R.drawable.ic_slot_1e,
                R.drawable.ic_slot_2e,
                R.drawable.ic_slot_3e,
            )
            imageSlot = R.layout.item_slot_miner
            imgSlot = R.id.imageSlotMiner
        }
    }

    inner class SlotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val slotImage: ImageView = itemView.findViewById(imgSlot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(imageSlot, parent, false)
        return SlotViewHolder(view)
    }

    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        val slotItem = defaultList[position]
        holder.slotImage.setImageResource(slotItem.image)
        holder.itemView.setOnClickListener {
            handleSlotClick(holder, position)
        }
    }

    override fun getItemCount(): Int = defaultList.size

    fun setSlotClickListener(listener: SlotClickListener) {
        itemSlotClickListener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateSlotList(newSlotList: List<Slot>) {
        defaultList = newSlotList
        notifyDataSetChanged()
        resetOpenSlotCount()
    }

    private fun handleSlotClick(holder: SlotViewHolder, position: Int) {
        if (openSlotCount < maxOpenSlot) {
            slotMutList?.shuffle()
            val randomImage = slotMutList?.randomOrNull()
            if (randomImage != null) {
                holder.slotImage.setImageResource(randomImage)
                itemSlotClickListener?.onItemClick(position, Slot(randomImage))
            }
            openSlotCount++
        }
    }

    private fun resetOpenSlotCount() {
        openSlotCount = 0
    }
}