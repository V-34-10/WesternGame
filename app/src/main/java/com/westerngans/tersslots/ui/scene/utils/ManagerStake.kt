package com.westerngans.tersslots.ui.scene.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import androidx.viewbinding.ViewBinding
import com.westerngans.tersslots.R
import com.westerngans.tersslots.databinding.FragmentFifeMinerGameBinding
import com.westerngans.tersslots.databinding.FragmentFirstGameBinding
import com.westerngans.tersslots.databinding.FragmentFourWheelGameBinding
import com.westerngans.tersslots.databinding.FragmentSecondGameBinding

class ManagerStake(
    private val sumStake: Int,
    private val minStake: Double,
    private val maxStake: Double,
    private val stepStake: Int
) {
    private var defaultStake = 300
    fun getDefaultStake(): Int = defaultStake

    fun isPlusStake(): Boolean = defaultStake < (sumStake * maxStake).toInt()

    fun plusStake() {
        if (isPlusStake()) defaultStake += stepStake
    }

    fun isMinusStake(): Boolean = defaultStake > (sumStake * minStake).toInt()

    fun minusStake() {
        if (isMinusStake()) defaultStake -= stepStake
    }
}

object UpdateStake {
    private lateinit var sharedPreferences: SharedPreferences
    fun init(sumStake: Int): ManagerStake {
        val minStake = 0.01
        val maxStake = 1.0
        val stepStake = 100
        return ManagerStake(sumStake, minStake, maxStake, stepStake)
    }

    fun numberFromText(text: String): Int = text.replace(Regex("\\D"), "").toIntOrNull() ?: 0

    fun updateStakeUI(binding: ViewBinding, stake: ManagerStake) {
        val defaultStake = stake.getDefaultStake()
        when (binding) {
            is FragmentFirstGameBinding -> {
                binding.statusTake.text = defaultStake.toString()
                binding.btnMinus.isEnabled = stake.isMinusStake()
                binding.btnPlus.isEnabled = stake.isPlusStake()
            }

            is FragmentSecondGameBinding -> {
                binding.statusTake?.text = defaultStake.toString()
                binding.btnMinus?.isEnabled = stake.isMinusStake()
                binding.btnPlus?.isEnabled = stake.isPlusStake()
            }

            is FragmentFourWheelGameBinding -> {
                binding.statusStake?.text = defaultStake.toString()
                binding.btnMinus?.isEnabled = stake.isMinusStake()
                binding.btnPlus?.isEnabled = stake.isPlusStake()
            }

            is FragmentFifeMinerGameBinding -> {
                binding.textBet.text = defaultStake.toString()
                binding.btnMinus.isEnabled = stake.isMinusStake()
                binding.btnPlus.isEnabled = stake.isPlusStake()
            }
        }
    }

    fun saveNewStake(context: Context, balance: String, bid: String?, win: String?) {
        sharedPreferences =
            context.getSharedPreferences("westernPref", AppCompatActivity.MODE_PRIVATE)
        sharedPreferences.edit().apply {
            putString("balance", balance)
            putString("bid", bid)
            putString("win", win)
            apply()
        }
    }

    fun setStake(context: Context): Triple<String?, String?, String?> {
        sharedPreferences =
            context.getSharedPreferences("westernPref", AppCompatActivity.MODE_PRIVATE)
        val balance = sharedPreferences.getString(
            "balance",
            context.getString(R.string.default_total_balance)
        )
        val bid = sharedPreferences.getString("bid", context.getString(R.string.default_stake))
        val win =
            sharedPreferences.getString("win", context.getString(R.string.default_win_fife_game))
        return Triple(balance, bid, win)
    }

    fun isStakeSave(context: Context): Boolean {
        sharedPreferences =
            context.getSharedPreferences("westernPref", AppCompatActivity.MODE_PRIVATE)
        return sharedPreferences.contains("balance")
    }
}