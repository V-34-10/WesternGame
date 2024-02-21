package com.westerngans.tersslots.ui.scene.game

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.RotateAnimation
import androidx.fragment.app.Fragment
import com.westerngans.tersslots.R
import com.westerngans.tersslots.databinding.FragmentFourWheelGameBinding
import com.westerngans.tersslots.ui.scene.utils.ManagerStake
import com.westerngans.tersslots.ui.scene.utils.SoundManager
import com.westerngans.tersslots.ui.scene.utils.UpdateStake
import java.util.Random

class FourWheelGameFragment : Fragment() {
    private lateinit var binding: FragmentFourWheelGameBinding
    private lateinit var managerStake: ManagerStake
    private val minAngleRotation = 0f
    private val maxAngleRotation = 720f
    private var currentAngle = 0f
    private lateinit var soundManager: SoundManager
    private lateinit var soundMainManager: SoundManager
    private var balance = 10000
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFourWheelGameBinding.inflate(layoutInflater, container, false)
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
            if (balance <= 0) {
                return@setOnClickListener
            }
            animationRotationWheel()
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

    private fun animationRotationWheel() {
        val randomAngle =
            minAngleRotation + Random().nextFloat() * (maxAngleRotation - minAngleRotation)
        val rotationAnimation = RotateAnimation(
            currentAngle,
            currentAngle + randomAngle,
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 2000
            fillAfter = true
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}

                override fun onAnimationEnd(animation: Animation?) {
                    currentAngle += randomAngle
                    summaryBalanceWin(currentAngle)
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })
        }
        binding.wheel?.startAnimation(rotationAnimation)
    }

    private fun normalAngleToDegrees(angle: Float): Float {
        val normalDegrees = angle % 360
        return if (normalDegrees < 0) normalDegrees + 360 else normalDegrees
    }

    private fun calcCoefficient(angle: Float): Int {
        return when (normalAngleToDegrees(angle)) {
            in 0f..36f -> 2 // 2x
            in 36f..72f -> 1 // 1x
            in 72f..108f -> 2 // 2x
            in 108f..144f -> 0 // 0x
            in 144f..180f -> 2 // 2x
            in 180f..216f -> 1 // 1x
            in 216f..252f -> 2 // 2x
            in 252f..288f -> 0 // 0x
            in 288f..324f -> 2 // 2x
            in 324f..360f -> 1 // 1x
            else -> {
                return 1
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun summaryBalanceWin(angle: Float) {
        val bid = UpdateStake.numberFromText(binding.statusStake?.text.toString())
        var win = UpdateStake.numberFromText(binding.textWin.text.toString())
        balance = UpdateStake.numberFromText(binding.textBalance?.text.toString())

        if (calcCoefficient(angle) == 0) {
            balance -= bid
            if (balance < 0) {
                balance = 0
            }
            binding.textBalance?.text = "Balance\n$balance"
            soundManager.playSound("lose")
        } else {
            val newSumWin = bid * calcCoefficient(angle)
            balance += newSumWin
            binding.textBalance?.text = "Balance\n$balance"
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

    override fun onDestroy() {
        super.onDestroy()
        soundMainManager.release()
        soundManager.release()
    }
}