package com.westerngans.tersslots.ui.scene.game

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.westerngans.tersslots.R
import com.westerngans.tersslots.databinding.FragmentFifeMinerGameBinding
import com.westerngans.tersslots.ui.scene.adapters.SlotClickListener
import com.westerngans.tersslots.ui.scene.adapters.SlotMinerAdapter
import com.westerngans.tersslots.ui.scene.model.Slot
import com.westerngans.tersslots.ui.scene.utils.ManagerStake
import com.westerngans.tersslots.ui.scene.utils.SoundManager
import com.westerngans.tersslots.ui.scene.utils.UpdateStake

class FifeMinerGameFragment : Fragment(), SlotClickListener {
    private lateinit var binding: FragmentFifeMinerGameBinding
    private lateinit var managerStake: ManagerStake
    private var defaultSlotMutList = MutableList(9) { R.drawable.ic_slot_1d }
    private lateinit var slotList: List<Slot>
    private lateinit var slotAdapter: SlotMinerAdapter
    private lateinit var soundManager: SoundManager
    private lateinit var soundMainManager: SoundManager
    private var balance = 10000
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFifeMinerGameBinding.inflate(layoutInflater, container, false)
        val sumBalance = UpdateStake.numberFromText(binding.textBalance.text.toString())
        managerStake = UpdateStake.init(sumBalance)

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

        UpdateStake.updateStakeUI(binding, managerStake)
        return binding.root
    }

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onResume() {
        super.onResume()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        soundMainManager.resume()
    }

    override fun onPause() {
        super.onPause()
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        soundMainManager.pause()
    }

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initSceneRecycler()
        gameControlButton()

        slotAdapter.setSlotClickListener(this)
        activity?.let { context ->
            if (UpdateStake.isStakeSave(context)) {
                val (restoredBalance) = UpdateStake.setStake(context)
                val balance = UpdateStake.numberFromText(restoredBalance.toString())
                binding.textBalance.text = "Balance $balance"
            }
        }
    }

    private fun gameControlButton() {
        var animation = AnimationUtils.loadAnimation(context, R.anim.scale)
        binding.btnPlay.setOnClickListener {
            it.startAnimation(animation)
            if (balance <= 0) {
                return@setOnClickListener
            }
            soundManager.playSound("click")
            slotList = defaultSlotMutList.map { Slot(it) }
            binding.sceneSlots.let { slotAdapter.updateSlotList(slotList) }
        }
        binding.btnMinus.setOnClickListener {
            animation = AnimationUtils.loadAnimation(context, R.anim.scale)
            it.startAnimation(animation)
            soundManager.playSound("click")
            managerStake.minusStake()
            UpdateStake.updateStakeUI(binding, managerStake)
        }
        binding.btnPlus.setOnClickListener {
            animation = AnimationUtils.loadAnimation(context, R.anim.scale)
            it.startAnimation(animation)
            soundManager.playSound("click")
            managerStake.plusStake()
            UpdateStake.updateStakeUI(binding, managerStake)
        }
        binding.btnBack.setOnClickListener {
            animation = AnimationUtils.loadAnimation(context, R.anim.scale)
            it.startAnimation(animation)
            soundManager.playSound("click")
            activity?.onBackPressed()
        }
    }

    private fun initSceneRecycler() {
        slotList = defaultSlotMutList.map { Slot(it) }
        slotAdapter = SlotMinerAdapter(slotList, 1)
        binding.sceneSlots.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = slotAdapter
        }
    }

    private fun calculateCoefficientWin(slot: Slot): Int {
        return when (slot.image) {
            R.drawable.ic_slot_2d -> 0
            R.drawable.ic_slot_3d -> 7
            R.drawable.ic_slot_4d -> 5
            R.drawable.ic_slot_5d -> 4
            R.drawable.ic_slot_6d -> 2
            R.drawable.ic_slot_7d -> 6
            R.drawable.ic_slot_8d -> 9
            else -> 0
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onItemClick(position: Int, slot: Slot) {
        if (position >= 0 && position < slotList.size) {
            val bid = UpdateStake.numberFromText(binding.textBet.text.toString())
            var win = UpdateStake.numberFromText(binding.textWin.text.toString())
            balance = UpdateStake.numberFromText(binding.textBalance.text.toString())

            if (calculateCoefficientWin(slot) == 0) {
                balance -= bid
                if (balance < 0) {
                    balance = 0
                }
                binding.textBalance.text = "Balance $balance"
                soundManager.playSound("lose")
            } else {
                val newSumWin = bid * calculateCoefficientWin(slot)
                balance += newSumWin
                binding.textBalance.text = "Balance $balance"
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
        } else {
            Log.e("FifeMinerGameFragment", "Invalid position: $position")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundMainManager.release()
        soundManager.release()
    }
}