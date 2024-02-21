package com.westerngans.tersslots.ui.scene.adapters

import androidx.recyclerview.widget.DiffUtil
import com.westerngans.tersslots.ui.scene.model.Slot

class SlotDiffCallback(private val defaultList: List<Slot>, private val newSlotList: List<Slot>) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int = defaultList.size

    override fun getNewListSize(): Int = newSlotList.size

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        defaultList[oldItemPosition].image == newSlotList[newItemPosition].image

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
        defaultList[oldItemPosition] == newSlotList[newItemPosition]
}