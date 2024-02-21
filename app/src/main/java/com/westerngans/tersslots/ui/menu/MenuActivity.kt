package com.westerngans.tersslots.ui.menu

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.westerngans.tersslots.R
import com.westerngans.tersslots.databinding.ActivityMenuBinding
import com.westerngans.tersslots.ui.scene.utils.SoundManager
import com.westerngans.tersslots.ui.settings.SettingsActivity
import com.westerngans.tersslots.utils.HideUI

class MenuActivity : AppCompatActivity() {
    private val binding by lazy { ActivityMenuBinding.inflate(layoutInflater) }
    private lateinit var startIntent: Intent
    private lateinit var soundManager: SoundManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        soundManager = SoundManager(this)
        soundManager.loadSound(this, "click", R.raw.click_button)

        HideUI.hideUI(this)
        navigationMenu()
    }

    private fun navigationMenu() {
        val animationClick = AnimationUtils.loadAnimation(this, R.anim.scale)
        binding.btnStart.setOnClickListener {
            binding.btnStart.startAnimation(animationClick)
            soundManager.playSound("click")
            startActivity(binding.btnStart)
        }
        binding.btnSettings.setOnClickListener {
            binding.btnSettings.startAnimation(animationClick)
            soundManager.playSound("click")
            startActivity(binding.btnSettings)
        }
    }

    @SuppressLint("NewApi")
    private fun startActivity(buttonClick: View) {
        when (buttonClick) {
            binding.btnStart -> {
                startIntent = Intent(this@MenuActivity, MenuGameActivity::class.java)
            }

            binding.btnSettings -> {
                startIntent = Intent(this@MenuActivity, SettingsActivity::class.java)
            }
        }
        startActivity(startIntent)
        finish()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundManager.release()
    }
}