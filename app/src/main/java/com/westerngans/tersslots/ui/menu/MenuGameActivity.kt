package com.westerngans.tersslots.ui.menu

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.westerngans.tersslots.R
import com.westerngans.tersslots.databinding.ActivityMenuGameBinding
import com.westerngans.tersslots.ui.scene.SceneActivity
import com.westerngans.tersslots.ui.scene.utils.SoundManager
import com.westerngans.tersslots.utils.HideUI

class MenuGameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMenuGameBinding
    private lateinit var startIntent: Intent
    private lateinit var listGameName: List<String>
    private lateinit var listButtonGame: List<ImageView>
    private var animation: Animation? = null
    private lateinit var soundManager: SoundManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMenuGameBinding.inflate(layoutInflater)
        setContentView(binding.root)
        animation = AnimationUtils.loadAnimation(this, R.anim.scale)

        soundManager = SoundManager(this)
        soundManager.loadSound(this, "click", R.raw.click_button)

        listGameName = listOf(
            getString(R.string.title_game_first),
            getString(R.string.title_game_second),
            getString(R.string.title_game_three),
            getString(R.string.title_game_four),
            getString(R.string.title_game_fife),
            getString(R.string.title_game_six),
            getString(R.string.title_game_seven)
        )
        listButtonGame = listOf(
            binding.btnGameFirst,
            binding.btnGameSecond,
            binding.btnGameThree,
            binding.btnGameFour,
            binding.btnGameFife,
            binding.btnGameSix,
            binding.btnGameSeven
        )
        HideUI.hideUI(this)
        controlRunGame()
    }

    private fun controlRunGame() {
        for ((i, buttonGame) in listButtonGame.withIndex()) {
            buttonGame.setOnClickListener {
                it.startAnimation(animation)
                soundManager.playSound("click")
                startIntent = Intent(this@MenuGameActivity, SceneActivity::class.java)
                startIntent.putExtra("nameGame", listGameName[i])
                startActivity(startIntent)
                finish()
            }
        }
        binding.btnBack.setOnClickListener {
            binding.btnBack.startAnimation(animation)
            soundManager.playSound("click")
            onBackPressed()
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        startIntent = Intent(this@MenuGameActivity, MenuActivity::class.java)
        startActivity(startIntent)
        super.onBackPressed()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        soundManager.release()
    }
}