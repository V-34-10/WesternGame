package com.westerngans.tersslots.ui.scene.game

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.westerngans.tersslots.R
import com.westerngans.tersslots.databinding.FragmentFirstGameBinding
import com.westerngans.tersslots.ui.scene.adapters.SlotAdapter
import com.westerngans.tersslots.ui.scene.model.Slot
import com.westerngans.tersslots.ui.scene.utils.ManagerStake
import com.westerngans.tersslots.ui.scene.utils.SoundManager
import com.westerngans.tersslots.ui.scene.utils.UpdateStake.init
import com.westerngans.tersslots.ui.scene.utils.UpdateStake.isStakeSave
import com.westerngans.tersslots.ui.scene.utils.UpdateStake.numberFromText
import com.westerngans.tersslots.ui.scene.utils.UpdateStake.saveNewStake
import com.westerngans.tersslots.ui.scene.utils.UpdateStake.setStake
import com.westerngans.tersslots.ui.scene.utils.UpdateStake.updateStakeUI

class FirstSlotsGameFragment : Fragment() {
    private lateinit var binding: FragmentFirstGameBinding
    private var slotMutList = mutableListOf(
        R.drawable.ic_slot_1,
        R.drawable.ic_slot_2,
        R.drawable.ic_slot_3,
        R.drawable.ic_slot_4,
        R.drawable.ic_slot_5,
        R.drawable.ic_slot_1,
        R.drawable.ic_slot_2,
        R.drawable.ic_slot_3,
        R.drawable.ic_slot_4
    )
    private lateinit var slotList: List<Slot>
    private lateinit var slotAdapter: SlotAdapter
    private var animationStart = false
    private lateinit var managerStake: ManagerStake
    private lateinit var soundManager: SoundManager
    private lateinit var soundMainManager: SoundManager
    private var balance = 10000

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFirstGameBinding.inflate(layoutInflater, container, false)
        val sumBalance = numberFromText(binding.textBalance.text.toString())
        managerStake = init(sumBalance)

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

        updateStakeUI(binding, managerStake)
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
        initSlotRecycler()
        gameControlButton()
        activity?.let { context ->
            if (isStakeSave(context)) {
                val (restoredBalance) = setStake(context)
                val balance = numberFromText(restoredBalance.toString())
                binding.textBalance.text = "Balance\n$balance"
            }
        }
    }

    private fun gameControlButton() {
        var animation = AnimationUtils.loadAnimation(context, R.anim.scale)
        binding.btnSpin.setOnClickListener {
            it.startAnimation(animation)
            if (animationStart || balance <= 0) {
                return@setOnClickListener
            }

            disabledAnimation()
            slotAdapter.updateSlotList(emptyList())

            animation?.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    animationStart = true
                }

                @SuppressLint("SetTextI18n")
                override fun onAnimationEnd(animation: Animation?) {
                    animationStart = false

                    val bid = numberFromText(binding.statusTake.text.toString())
                    var win = numberFromText(binding.textWin.text.toString())
                    balance = numberFromText(binding.textBalance.text.toString())

                    if (calcCoefficient(slotList).toInt() == 0) {
                        balance -= bid
                        if (balance < 0) {
                            balance = 0
                        }
                        binding.textBalance.text = "Balance\n$balance"
                        soundManager.playSound("lose")
                    } else {
                        val newSumWin = bid * calcCoefficient(slotList)
                        balance += newSumWin.toInt()
                        binding.textBalance.text = "Balance\n$balance"
                        win += newSumWin.toInt()
                        binding.textWin.text = "WIN\n$win"
                        soundManager.playSound("win")
                    }
                    activity?.let { it1 ->
                        saveNewStake(
                            it1,
                            "Balance\n$balance",
                            bid.toString(),
                            "WIN\n$win"
                        )
                    }
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })
            generateRandomSlotList()
            slotAdapter.updateSlotList(slotList)
            slotAdapter.runSpinAnimation(binding.sceneSlots)

        }
        binding.btnMinus.setOnClickListener {
            animation = AnimationUtils.loadAnimation(context, R.anim.scale)
            it.startAnimation(animation)
            soundManager.playSound("click")
            managerStake.minusStake()
            updateStakeUI(binding, managerStake)
        }
        binding.btnPlus.setOnClickListener {
            animation = AnimationUtils.loadAnimation(context, R.anim.scale)
            it.startAnimation(animation)
            soundManager.playSound("click")
            managerStake.plusStake()
            updateStakeUI(binding, managerStake)
        }
        binding.btnBack.setOnClickListener {
            animation = AnimationUtils.loadAnimation(context, R.anim.scale)
            it.startAnimation(animation)
            soundManager.playSound("click")
            activity?.onBackPressed()
        }
    }

    private fun generateRandomSlotList() {
        slotMutList.shuffle()
        slotList = slotMutList.map { Slot(it) }
    }

    private fun calcCoefficient(slotList: List<Slot>): Float {
        val middlePosition = slotList.subList(3, 6)
        val uniqueList = middlePosition.distinct()
        return when (uniqueList.size) {
            1 -> 3.5f
            2 -> 1.5f
            else -> 0.0f
        }
    }

    private fun disabledAnimation() {
        val slotCount = slotAdapter.itemCount
        for (i in 0 until slotCount) {
            val holder =
                binding.sceneSlots.findViewHolderForAdapterPosition(i) as? SlotAdapter.SlotViewHolder
            holder?.itemView?.clearAnimation()
        }
    }

    private fun initSlotRecycler() {
        slotList = slotMutList.map { Slot(it) }
        slotAdapter = SlotAdapter(slotList)
        binding.sceneSlots.apply {
            layoutManager = GridLayoutManager(context, 3)
            adapter = slotAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundMainManager.release()
        soundManager.release()
    }
}