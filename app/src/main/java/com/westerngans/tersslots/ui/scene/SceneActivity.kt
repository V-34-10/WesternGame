package com.westerngans.tersslots.ui.scene

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.westerngans.tersslots.R
import com.westerngans.tersslots.databinding.ActivitySceneBinding
import com.westerngans.tersslots.ui.menu.MenuGameActivity
import com.westerngans.tersslots.ui.scene.game.FifeMinerGameFragment
import com.westerngans.tersslots.ui.scene.game.FirstSlotsGameFragment
import com.westerngans.tersslots.ui.scene.game.FourWheelGameFragment
import com.westerngans.tersslots.ui.scene.game.SecondSlotsGameFragment
import com.westerngans.tersslots.ui.scene.game.SevenBonesGameFragment
import com.westerngans.tersslots.ui.scene.game.SixMinerStompedGameFragment
import com.westerngans.tersslots.ui.scene.game.ThreeMinerGameFragment
import com.westerngans.tersslots.utils.HideUI

class SceneActivity : AppCompatActivity() {
    private val binding by lazy { ActivitySceneBinding.inflate(layoutInflater) }
    private val bundle = Bundle()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        HideUI.hideUI(this)
        initFragment()
    }

    @SuppressLint("CommitTransaction")
    private fun initFragment() {
        val nameExtraGame = intent.getStringExtra("nameGame")
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        val fragmentGame: Fragment

        when (nameExtraGame) {
            getString(R.string.title_game_first) -> {
                fragmentGame = FirstSlotsGameFragment()
                bundle.putString("nameGame", getString(R.string.title_game_first))
                fragmentGame.arguments = bundle
                fragmentTransaction.replace(R.id.container_games, fragmentGame)
            }

            getString(R.string.title_game_second) -> {
                fragmentGame = SecondSlotsGameFragment()
                bundle.putString("nameGame", getString(R.string.title_game_second))
                fragmentGame.arguments = bundle
                fragmentTransaction.replace(R.id.container_games, fragmentGame)
            }

            getString(R.string.title_game_three) -> {
                fragmentGame = ThreeMinerGameFragment()
                bundle.putString("nameGame", getString(R.string.title_game_three))
                fragmentGame.arguments = bundle
                fragmentTransaction.replace(R.id.container_games, fragmentGame)
            }

            getString(R.string.title_game_four) -> {
                fragmentGame = FourWheelGameFragment()
                bundle.putString("nameGame", getString(R.string.title_game_four))
                fragmentGame.arguments = bundle
                fragmentTransaction.replace(R.id.container_games, fragmentGame)
            }

            getString(R.string.title_game_fife) -> {
                fragmentGame = FifeMinerGameFragment()
                bundle.putString("nameGame", getString(R.string.title_game_fife))
                fragmentGame.arguments = bundle
                fragmentTransaction.replace(R.id.container_games, fragmentGame)
            }

            getString(R.string.title_game_six) -> {
                fragmentGame = SixMinerStompedGameFragment()
                bundle.putString("nameGame", getString(R.string.title_game_six))
                fragmentGame.arguments = bundle
                fragmentTransaction.replace(R.id.container_games, fragmentGame)
            }

            getString(R.string.title_game_seven) -> {
                fragmentGame = SevenBonesGameFragment()
                bundle.putString("nameGame", getString(R.string.title_game_seven))
                fragmentGame.arguments = bundle
                fragmentTransaction.replace(R.id.container_games, fragmentGame)
            }
        }
        fragmentTransaction.commit()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            this.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
            Intent(this@SceneActivity, MenuGameActivity::class.java).also {
                startActivity(it)
                super.onBackPressed()
                finish()
            }
        }
    }
}