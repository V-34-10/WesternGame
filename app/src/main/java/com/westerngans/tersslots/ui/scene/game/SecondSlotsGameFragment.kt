package com.westerngans.tersslots.ui.scene.game

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.westerngans.tersslots.R
import com.westerngans.tersslots.databinding.FragmentSecondGameBinding
import com.westerngans.tersslots.ui.scene.adapters.SlotAdapter
import com.westerngans.tersslots.ui.scene.model.Slot
import com.westerngans.tersslots.ui.scene.utils.ManagerStake
import com.westerngans.tersslots.ui.scene.utils.SoundManager
import com.westerngans.tersslots.ui.scene.utils.UpdateStake

class SecondSlotsGameFragment : Fragment() {
    private lateinit var binding: FragmentSecondGameBinding
    private var slotMutList = mutableListOf(
        R.drawable.ic_slot_1b,
        R.drawable.ic_slot_2b,
        R.drawable.ic_slot_3b,
        R.drawable.ic_slot_4b,
        R.drawable.ic_slot_5b,
        R.drawable.ic_slot_1b,
        R.drawable.ic_slot_2b,
        R.drawable.ic_slot_3b,
        R.drawable.ic_slot_4b,
        R.drawable.ic_slot_5b,
        R.drawable.ic_slot_1b,
        R.drawable.ic_slot_2b
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
        binding = FragmentSecondGameBinding.inflate(layoutInflater, container, false)
        val sumBalance = UpdateStake.numberFromText(binding.textBalance?.text.toString())
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

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initSlotRecycler()
        gameControlButton()
        activity?.let { context ->
            if (UpdateStake.isStakeSave(context)) {
                val (restoredBalance) = UpdateStake.setStake(context)
                val balance = UpdateStake.numberFromText(restoredBalance.toString())
                binding.textBalance?.text = "Balance\n$balance"
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

    private fun gameControlButton() {
        var animation = AnimationUtils.loadAnimation(context, R.anim.scale)
        binding.btnSpin?.setOnClickListener {
            it!!.startAnimation(animation)
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

                    val bid = UpdateStake.numberFromText(binding.statusTake?.text.toString())
                    var win = UpdateStake.numberFromText(binding.textWin.text.toString())
                    balance = UpdateStake.numberFromText(binding.textBalance?.text.toString())

                    if (calcCoefficient(slotList).toInt() == 0) {
                        balance -= bid
                        if (balance < 0) {
                            balance = 0
                        }
                        binding.textBalance?.text = "Balance\n$balance"
                        soundManager.playSound("lose")
                    } else {
                        val newSumWin = bid * calcCoefficient(slotList)
                        balance += newSumWin.toInt()
                        binding.textBalance?.text = "Balance\n$balance"
                        win += newSumWin.toInt()
                        binding.textWin.text = "WIN\n$win"
                        soundManager.playSound("win")
                    }
                    activity?.let { it1 ->
                        UpdateStake.saveNewStake(
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
            binding.sceneSlots?.let { it1 -> slotAdapter.runSpinAnimation(it1) }

        }
        binding.btnMinus?.setOnClickListener {
            animation = AnimationUtils.loadAnimation(context, R.anim.scale)
            it!!.startAnimation(animation)
            soundManager.playSound("click")
            managerStake.minusStake()
            UpdateStake.updateStakeUI(binding, managerStake)
        }
        binding.btnPlus?.setOnClickListener {
            animation = AnimationUtils.loadAnimation(context, R.anim.scale)
            it!!.startAnimation(animation)
            soundManager.playSound("click")
            managerStake.plusStake()
            UpdateStake.updateStakeUI(binding, managerStake)
        }
        binding.btnBack?.setOnClickListener {
            animation = AnimationUtils.loadAnimation(context, R.anim.scale)
            it!!.startAnimation(animation)
            soundManager.playSound("click")
            activity?.onBackPressed()
        }
    }

    private fun generateRandomSlotList() {
        slotMutList.shuffle()
        slotList = slotMutList.map { Slot(it) }
    }

    private fun calcCoefficient(slotList: List<Slot>): Float {
        return if (slotList.distinct().size >= 3) 2.5f else 0.0f
    }

    private fun disabledAnimation() {
        val slotCount = slotAdapter.itemCount
        for (i in 0 until slotCount) {
            val holder =
                binding.sceneSlots?.findViewHolderForAdapterPosition(i) as? SlotAdapter.SlotViewHolder
            holder?.itemView?.clearAnimation()
        }
    }

    private fun initSlotRecycler() {
        slotList = slotMutList.map { Slot(it) }
        slotAdapter = SlotAdapter(slotList)
        binding.sceneSlots?.apply {
            layoutManager = GridLayoutManager(context, 4)
            adapter = slotAdapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundMainManager.release()
        soundManager.release()
    }
}