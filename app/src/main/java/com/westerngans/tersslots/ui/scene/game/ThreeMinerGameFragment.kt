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
import com.westerngans.tersslots.R
import com.westerngans.tersslots.databinding.FragmentThreeGameBinding
import com.westerngans.tersslots.ui.scene.model.Slot
import com.westerngans.tersslots.ui.scene.utils.SoundManager
import com.westerngans.tersslots.ui.scene.utils.UpdateStake

class ThreeMinerGameFragment : Fragment() {
    private lateinit var binding: FragmentThreeGameBinding
    private var slotMutList = mutableListOf(
        R.drawable.ic_slot_2c,
        R.drawable.ic_slot_3c,
        R.drawable.ic_slot_1c
    )
    private var slotCorrectList = mutableListOf(
        R.drawable.ic_slot_2c,
        R.drawable.ic_slot_3c,
        R.drawable.ic_slot_1c
    )
    private lateinit var buttonBid: List<Pair<View, Int>>
    private lateinit var slotList: List<Slot>
    private var animationStart = false
    private var randomIndex: Int = 0
    private lateinit var soundManager: SoundManager
    private lateinit var soundMainManager: SoundManager
    private var balance = 10000
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentThreeGameBinding.inflate(layoutInflater, container, false)

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
        buttonBid = listOf(
            binding.btnSetStakeFirst to 50,
            binding.btnSetStakeSecond to 100,
            binding.btnSetStakeThree to 150,
            binding.btnSetStakeFour to 200,
            binding.btnSetStakeFife to 250,
            binding.btnSetStakeSix to 300
        )
        stakeButton()
        spinButton()
        activity?.let { context ->
            if (UpdateStake.isStakeSave(context)) {
                val (restoredBalance) = UpdateStake.setStake(context)
                val balance = UpdateStake.numberFromText(restoredBalance.toString())
                binding.textBalance.text = "Balance\n$balance"
            }
        }
    }

    private fun stakeButton() {
        val animation = AnimationUtils.loadAnimation(context, R.anim.scale)
        buttonBid.forEach { (button, bidValue) ->
            button.setOnClickListener {
                it.startAnimation(animation)
                soundManager.playSound("click")
                updateStake(bidValue)
            }
        }
    }

    private fun spinButton() {
        var animation = AnimationUtils.loadAnimation(context, R.anim.scale)
        binding.btnSpin.setOnClickListener {
            it.startAnimation(animation)
            if (animationStart || balance <= 0) {
                return@setOnClickListener
            }

            animation?.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {
                    animationStart = true
                }

                @SuppressLint("SetTextI18n")
                override fun onAnimationEnd(animation: Animation?) {
                    animationStart = false
                    generateSlotList()
                    handleWinCalculation()
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })
        }
        binding.btnBack.setOnClickListener {
            animation = AnimationUtils.loadAnimation(context, R.anim.scale)
            it.startAnimation(animation)
            soundManager.playSound("click")
            activity?.onBackPressed()
        }
    }

    private fun updateStake(bidValue: Int) {
        binding.statusStake.text = bidValue.toString()
    }

    @SuppressLint("SetTextI18n")
    private fun handleWinCalculation() {
        val bid = UpdateStake.numberFromText(binding.statusStake.text.toString())
        var win = UpdateStake.numberFromText(binding.textWin.text.toString())
        balance = UpdateStake.numberFromText(binding.textBalance.text.toString())

        if (winCoefficient(slotList) == 0) {
            balance -= bid
            if (balance < 0) {
                balance = 0
            }
            binding.textBalance.text = "Balance\n$balance"
            soundManager.playSound("lose")
        } else {
            val coefficient = winCoefficient(slotList)
            val newSumWin = bid * coefficient
            balance += newSumWin
            binding.textBalance.text = "Balance\n$balance"
            win += newSumWin
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

    private fun generateSlotList() {
        randomIndex = slotCorrectList.indices.random()
        val newImageResource = slotCorrectList[randomIndex]
        slotMutList[1] = newImageResource
        binding.btnSlotSecond.setImageResource(slotMutList[1])
        slotList = slotMutList.map { Slot(it) }
    }

    private fun winCoefficient(slotList: List<Slot>): Int {
        val middlePosition = slotList.subList(1, 3)
        val uniqueList = middlePosition.distinct()
        return when (uniqueList.size) {
            1 -> {
                if (uniqueList[0].image == R.drawable.ic_slot_3c) {
                    0
                } else {
                    when {
                        middlePosition[0].image == middlePosition[1].image && middlePosition[0].image == R.drawable.ic_slot_1c -> 2
                        middlePosition[0].image == middlePosition[1].image && middlePosition[0].image == R.drawable.ic_slot_2c -> 1
                        else -> 0
                    }
                }
            }

            else -> 0
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        soundMainManager.release()
        soundManager.release()
    }
}