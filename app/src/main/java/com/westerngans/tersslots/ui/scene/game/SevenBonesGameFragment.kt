package com.westerngans.tersslots.ui.scene.game

import android.annotation.SuppressLint
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.westerngans.tersslots.R
import com.westerngans.tersslots.databinding.FragmentSevenBonesGameBinding
import com.westerngans.tersslots.ui.scene.utils.SoundManager
import com.westerngans.tersslots.ui.scene.utils.UpdateStake

class SevenBonesGameFragment : Fragment() {
    private lateinit var binding: FragmentSevenBonesGameBinding
    private var slotMutList = mutableListOf(
        R.drawable.ic_bones_1,
        R.drawable.ic_bones_2,
        R.drawable.ic_bones_3,
        R.drawable.ic_bones_4,
        R.drawable.ic_bones_5,
        R.drawable.ic_bones_6
    )
    private var animationStart = false
    private lateinit var soundManager: SoundManager
    private lateinit var soundMainManager: SoundManager
    private var balance = 10000
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSevenBonesGameBinding.inflate(layoutInflater, container, false)

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
        gameControlButton()
        buttonBid()
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
        binding.btnSpin?.setOnClickListener {
            it!!.startAnimation(animation)
            generateRandomSlotList()
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

                    val bid = UpdateStake.numberFromText(binding.textBet?.text.toString())
                    var win = UpdateStake.numberFromText(binding.textWin.text.toString())
                    balance = UpdateStake.numberFromText(binding.textBalance?.text.toString())

                    if (binding.bonesSecond?.let { it1 ->
                            binding.bonesFirst?.let { it2 ->
                                calcCoefficient(
                                    it2,
                                    it1
                                )
                            }
                        } == 0
                    ) {
                        balance -= bid
                        if (balance < 0) {
                            balance = 0
                        }
                        binding.textBalance?.text = "Balance $balance"
                        soundManager.playSound("lose")

                    } else if (binding.bonesSecond?.let { it1 ->
                            binding.bonesFirst?.let { it2 ->
                                calcCoefficient(
                                    it2,
                                    it1
                                )
                            }
                        } == 200) {
                        balance += 200
                        binding.textBalance?.text = "Balance $balance"
                        soundManager.playSound("win")
                    } else {
                        val coefficient = binding.bonesSecond?.let { it1 ->
                            binding.bonesFirst?.let { it2 ->
                                calcCoefficient(
                                    it2,
                                    it1
                                )
                            }
                        }
                        val newSumWin = bid * coefficient!!
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

                override fun onAnimationRepeat(animation: Animation?) {}
            })
        }
        binding.btnInfo?.setOnClickListener {
            animation = AnimationUtils.loadAnimation(context, R.anim.scale)
            it!!.startAnimation(animation)
            soundManager.playSound("click")
            runDialogInfo()
        }
        binding.btnBack?.setOnClickListener {
            animation = AnimationUtils.loadAnimation(context, R.anim.scale)
            it!!.startAnimation(animation)
            soundManager.playSound("click")
            activity?.onBackPressed()
        }
    }

    @SuppressLint("SetTextI18n")
    private fun buttonBid() {
        val animation = AnimationUtils.loadAnimation(context, R.anim.scale)
        val buttonBid = listOf(
            binding.btnStakeFirst to 100,
            binding.btnStakeSecond to 200,
            binding.btnStakeSecondThree to 300,
            binding.btnStakeFour to 400,
            binding.btnStakeFife to 500,
            binding.btnStakeSix to 600,
            binding.btnStakeSeven to 700
        )
        buttonBid.forEach { (button, bidValue) ->
            button?.setOnClickListener {
                button.startAnimation(animation)
                soundManager.playSound("click")
                binding.textBet?.text = "BET $bidValue"
            }
        }
    }

    private fun generateRandomSlotList() {
        slotMutList.shuffle()
        val randomIndexFirst = slotMutList.indices.random()
        binding.bonesFirst?.setImageResource(slotMutList[randomIndexFirst])
        binding.bonesFirst?.tag = slotMutList[randomIndexFirst]

        val randomIndexSecond = slotMutList.indices.random()
        binding.bonesSecond?.setImageResource(slotMutList[randomIndexSecond])
        binding.bonesSecond?.tag = slotMutList[randomIndexSecond]
    }

    private fun calcCoefficient(
        firstImageView: ImageView,
        secondImageView: ImageView
    ): Int {
        val image1 = firstImageView.tag as Int
        val image2 = secondImageView.tag as Int
        val coefficientMap = mapOf(
            Pair(R.drawable.ic_bones_1, R.drawable.ic_bones_1) to 0,
            Pair(R.drawable.ic_bones_1, R.drawable.ic_bones_4) to 0,
            Pair(R.drawable.ic_bones_2, R.drawable.ic_bones_6) to 0,
            Pair(R.drawable.ic_bones_1, R.drawable.ic_bones_2) to 2,
            Pair(R.drawable.ic_bones_1, R.drawable.ic_bones_3) to 2,
            Pair(R.drawable.ic_bones_1, R.drawable.ic_bones_5) to 3,
            Pair(R.drawable.ic_bones_2, R.drawable.ic_bones_2) to 3,
            Pair(R.drawable.ic_bones_1, R.drawable.ic_bones_6) to 1,
            Pair(R.drawable.ic_bones_4, R.drawable.ic_bones_1) to 1,
            Pair(R.drawable.ic_bones_5, R.drawable.ic_bones_1) to 1,
            Pair(R.drawable.ic_bones_2, R.drawable.ic_bones_3) to 4,
            Pair(R.drawable.ic_bones_4, R.drawable.ic_bones_4) to 4,
            Pair(R.drawable.ic_bones_3, R.drawable.ic_bones_3) to 5,
            Pair(R.drawable.ic_bones_4, R.drawable.ic_bones_6) to 5,
            Pair(R.drawable.ic_bones_6, R.drawable.ic_bones_4) to 5,
            Pair(R.drawable.ic_bones_3, R.drawable.ic_bones_4) to 200
        )
        val key = Pair(image1, image2)
        return coefficientMap[key] ?: 0
    }

    private fun runDialogInfo() {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(R.layout.dialog_info_seven)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundMainManager.release()
        soundManager.release()
    }
}