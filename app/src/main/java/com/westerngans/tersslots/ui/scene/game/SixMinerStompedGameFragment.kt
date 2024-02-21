package com.westerngans.tersslots.ui.scene.game

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.westerngans.tersslots.R
import com.westerngans.tersslots.databinding.FragmentSixMinerSecondGameBinding
import com.westerngans.tersslots.ui.scene.adapters.SlotClickListener
import com.westerngans.tersslots.ui.scene.adapters.SlotMinerAdapter
import com.westerngans.tersslots.ui.scene.model.Slot
import com.westerngans.tersslots.ui.scene.utils.SoundManager
import com.westerngans.tersslots.ui.scene.utils.UpdateStake

class SixMinerStompedGameFragment : Fragment(), SlotClickListener {

    private lateinit var binding: FragmentSixMinerSecondGameBinding
    private var defaultSlotMutList = MutableList(30) { R.drawable.ic_slot_4e }
    private lateinit var slotList: List<Slot>
    private lateinit var slotAdapter: SlotMinerAdapter
    private lateinit var soundManager: SoundManager
    private lateinit var soundMainManager: SoundManager
    private var balance = 10000
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSixMinerSecondGameBinding.inflate(layoutInflater, container, false)

        soundManager = SoundManager(requireContext())
        soundManager.apply {
            loadSound(requireContext(), "click", R.raw.click_button)
            loadSound(requireContext(), "win", R.raw.plus_to_balance)
            loadSound(requireContext(), "lose", R.raw.minus_with_balance)
        }

        soundMainManager = SoundManager(requireContext())
        soundMainManager.apply {
            loadSound(requireContext(), "background", R.raw.casino_slots_3)
            playSound("background", true)
        }

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSlotRecycler()
        gameControlButton()
        slotAdapter.setSlotClickListener(this)
        activity?.let { context ->
            if (UpdateStake.isStakeSave(context)) {
                val (restoredBalance) = UpdateStake.setStake(context)
                val balance = UpdateStake.numberFromText(restoredBalance.toString())
                binding.textBalance?.text = "Balance $balance"
            }
        }
    }

    override fun onResume() {
        super.onResume()
        soundMainManager.resume()
    }

    override fun onPause() {
        super.onPause()
        soundMainManager.pause()
    }

    @SuppressLint("SetTextI18n")
    private fun gameControlButton() {
        var animation = AnimationUtils.loadAnimation(context, R.anim.scale)
        val buttonBid = listOf(
            binding.btnStakeFirst to 50,
            binding.btnStakeSecond to 100,
            binding.btnStakeSecondThree to 200,
            binding.btnStakeFour to 300
        )
        binding.btnSpin?.setOnClickListener {
            it!!.startAnimation(animation)
            if (balance <= 0) {
                return@setOnClickListener
            }
            soundManager.playSound("click")
            slotList = defaultSlotMutList.map { Slot(it) }
            binding.sceneSlots.let { slotAdapter.updateSlotList(slotList) }
        }
        buttonBid.forEach { (button, bidValue) ->
            button?.setOnClickListener {
                animation = AnimationUtils.loadAnimation(context, R.anim.scale)
                button.startAnimation(animation)
                soundManager.playSound("click")
                binding.textBet?.text = "BET $bidValue"
            }
        }
        binding.btnBack?.setOnClickListener {
            animation = AnimationUtils.loadAnimation(context, R.anim.scale)
            it!!.startAnimation(animation)
            soundManager.playSound("click")
            activity?.onBackPressed()
        }
    }

    private fun initSlotRecycler() {
        slotList = defaultSlotMutList.map { Slot(it) }
        slotAdapter = SlotMinerAdapter(slotList, 3)
        binding.sceneSlots?.apply {
            layoutManager = GridLayoutManager(context, 5)
            adapter = slotAdapter
        }
    }

    private fun calculateCoefficientWin(slot: Slot): Int {
        return when (slot.image) {
            R.drawable.ic_slot_2e -> 0
            R.drawable.ic_slot_1e -> 100
            R.drawable.ic_slot_3e -> 500
            else -> 0
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onItemClick(position: Int, slot: Slot) {
        val bid = UpdateStake.numberFromText(binding.textBet?.text.toString())
        var win = UpdateStake.numberFromText(binding.textWin.text.toString())
        balance = UpdateStake.numberFromText(binding.textBalance?.text.toString())

        if (calculateCoefficientWin(slot) == 0) {
            balance -= bid
            if (balance < 0) {
                balance = 0
            }
            binding.textBalance?.text = "Balance $balance"
            soundManager.playSound("lose")
        } else {
            val newSumWin = bid + calculateCoefficientWin(slot)
            balance += newSumWin
            binding.textBalance?.text = "Balance $balance"
            win += newSumWin
            binding.textWin.text = "WIN $win"
            soundManager.playSound("win")
        }
        activity?.let { it1 ->
            UpdateStake.saveNewStake(
                it1,
                "Balance $balance",
                bid.toString(),
                "WIN $win"
            )
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundMainManager.release()
        soundManager.release()
    }
}