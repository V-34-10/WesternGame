package com.westerngans.tersslots.ui.scene.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.westerngans.tersslots.R
import com.westerngans.tersslots.ui.scene.model.Slot

class SlotAdapter(private var slotList: List<Slot>) :
    RecyclerView.Adapter<SlotAdapter.SlotViewHolder>() {
    private var defaultSlotList: List<Slot> = emptyList()

    inner class SlotViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val slotImageView: ImageView = itemView.findViewById(R.id.imageSlot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlotViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_slot, parent, false)
        return SlotViewHolder(view)
    }

    override fun getItemCount(): Int = slotList.size

    override fun onBindViewHolder(holder: SlotViewHolder, position: Int) {
        val slot = slotList[position]
        holder.slotImageView.setImageResource(slot.image)
        holder.itemView.startAnimation(
            AnimationUtils.loadAnimation(holder.itemView.context, R.anim.animation_slot)
        )
    }

    fun updateSlotList(newSlotList: List<Slot>) {
        defaultSlotList = slotList.toList()
        slotList = newSlotList
        val diffCallback = SlotDiffCallback(defaultSlotList, newSlotList)
        val result = DiffUtil.calculateDiff(diffCallback)
        result.dispatchUpdatesTo(this)
    }

    fun runSpinAnimation(recyclerView: RecyclerView) {
        for (i in 0 until itemCount) {
            val holder = recyclerView.findViewHolderForAdapterPosition(i) as? SlotViewHolder
            holder?.itemView?.startAnimation(
                AnimationUtils.loadAnimation(holder.itemView.context, R.anim.animation_slot)
            )
        }
    }
}