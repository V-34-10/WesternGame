package com.westerngans.tersslots.ui.scene.adapters

import com.westerngans.tersslots.ui.scene.model.Slot

interface SlotClickListener {
    fun onItemClick(position: Int, slot: Slot)
}