package com.westerngans.tersslots.ui.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.GradientDrawable
import android.media.AudioManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.westerngans.tersslots.R
import com.westerngans.tersslots.databinding.ActivitySettingsBinding
import com.westerngans.tersslots.ui.menu.MenuActivity
import com.westerngans.tersslots.ui.scene.utils.SoundManager
import com.westerngans.tersslots.utils.HideUI

@RequiresApi(Build.VERSION_CODES.S)
@SuppressLint("ServiceCast")
class SettingsActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySettingsBinding.inflate(layoutInflater) }
    private lateinit var sharedPreferences: SharedPreferences
    private val managerAudio by lazy { getSystemService(Context.AUDIO_SERVICE) as AudioManager }
    private var defaultVolume: Int = 0
    private val managerVibration by lazy { getSystemService(Context.VIBRATOR_SERVICE) as Vibrator }
    private var musicOn = false
    private var vibrationOn = false
    private lateinit var startIntent: Intent
    private var animation: Animation? = null
    private lateinit var soundManager: SoundManager
    private var lastClickTime: Long = 0
    private var currentTime: Long = System.currentTimeMillis()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        animation = AnimationUtils.loadAnimation(this, R.anim.scale)

        soundManager = SoundManager(this)
        soundManager.loadSound(this, "click", R.raw.click_button)

        HideUI.hideUI(this)
        controlSounds()
        setSoundVibration()
    }

    private fun controlSounds() {
        binding.btnRemove.setOnClickListener {
            it.startAnimation(animation)
            soundManager.playSound("click")
            removeAccount()
            Toast.makeText(applicationContext, "Balance default", Toast.LENGTH_SHORT).show()
        }
        binding.btnBack.setOnClickListener {
            it.startAnimation(animation)
            soundManager.playSound("click")
            onBackPressed()
        }
    }

    private fun setSoundVibration() {
        setElipsColor(musicOn, binding.progressMusic.progressBarGroup)
        setElipsColor(vibrationOn, binding.progressVibration.progressBarGroup)
        binding.progressMusic.progressBarGroup.setOnClickListener {
            soundManager.playSound("click")
            onSound()
        }
        binding.progressVibration.progressBarGroup.setOnClickListener {
            soundManager.playSound("click")
            onVibration()
        }
    }

    private fun onSound() {
        currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < 500) {
            return
        }
        lastClickTime = currentTime

        musicOn = !musicOn
        when (musicOn) {
            true -> enableSound()
            else -> disableSound()
        }
        runOnUiThread {
            setElipsColor(musicOn, binding.progressMusic.progressBarGroup)
        }
    }

    private fun onVibration() {
        currentTime = System.currentTimeMillis()
        if (currentTime - lastClickTime < 500) {
            return
        }
        lastClickTime = currentTime

        vibrationOn = !vibrationOn
        when (vibrationOn) {
            true -> enableVibration()
            else -> disableVibration()
        }
        runOnUiThread {
            setElipsColor(vibrationOn, binding.progressVibration.progressBarGroup)
        }
    }

    @SuppressLint("DiscouragedApi")
    private fun setElipsColor(active: Boolean, progressBar: ViewGroup) {
        val elipsColor = if (active) {
            resources.getColor(R.color.light_blue)
        } else {
            resources.getColor(R.color.blue)
        }
        for (i in 1..8) {
            val elipsId = resources.getIdentifier("elips$i", "id", packageName)
            val elipsView = progressBar.findViewById<View>(elipsId)
            val elipsDrawable = elipsView.background as? GradientDrawable
            elipsDrawable?.setColor(elipsColor)
            elipsView.invalidate()
        }
    }

    private fun enableSound() {
        managerAudio.setStreamVolume(AudioManager.STREAM_MUSIC, defaultVolume, 0)
    }

    private fun disableSound() {
        defaultVolume = managerAudio.getStreamVolume(AudioManager.STREAM_MUSIC)
        managerAudio.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0)
    }

    private fun enableVibration() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            managerVibration.vibrate(
                VibrationEffect.createOneShot(
                    1500,
                    VibrationEffect.DEFAULT_AMPLITUDE
                )
            )
        } else {
            managerVibration.vibrate(1500)
        }
    }

    private fun disableVibration() {
        managerVibration.cancel()
    }

    private fun removeAccount() {
        sharedPreferences = getSharedPreferences("westernPref", MODE_PRIVATE)
        sharedPreferences.edit().putString("balance", getString(R.string.default_total_balance))
            .apply()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        startIntent = Intent(this@SettingsActivity, MenuActivity::class.java)
        startActivity(startIntent)
        finish()
    }
}