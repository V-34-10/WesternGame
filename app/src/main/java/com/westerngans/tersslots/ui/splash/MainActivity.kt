package com.westerngans.tersslots.ui.splash

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.westerngans.tersslots.R
import com.westerngans.tersslots.databinding.ActivityMainBinding
import com.westerngans.tersslots.ui.menu.MenuActivity
import com.westerngans.tersslots.ui.privacy.PrivacyActivity
import com.westerngans.tersslots.utils.HideUI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.CoroutineContext

class MainActivity : AppCompatActivity(), CoroutineScope {
    private val binding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private var elipsIndex = 1
    private lateinit var jobLoading: Job
    override val coroutineContext: CoroutineContext get() = Dispatchers.Main + jobLoading
    private lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        jobLoading = Job()
        HideUI.hideUI(this)
        runNextActivity()
        animateProgressBar()
    }

    private fun runNextActivity() {
        saveStatePrivacy()
        val flagPrivacy = sharedPreferences.getBoolean("statePrivacy", false)
        var startIntent: Intent
        Handler().postDelayed({
            if (!flagPrivacy) {
                startIntent = Intent(this@MainActivity, PrivacyActivity::class.java)
                startActivity(startIntent)
                val editor = sharedPreferences.edit()
                editor.putBoolean("statePrivacy", true)
                editor.apply()
            } else {
                startIntent = Intent(this@MainActivity, MenuActivity::class.java)
                startActivity(startIntent)
            }
            finish()
        }, 3 * 1000.toLong())
    }

    private fun saveStatePrivacy() {
        sharedPreferences = getSharedPreferences("westernPref", MODE_PRIVATE)
        val flagPrivacy = sharedPreferences.getBoolean("statePrivacy", false)
        val editor = sharedPreferences.edit()
        editor.putBoolean("statePrivacy", flagPrivacy)
        editor.apply()
    }

    private fun animateProgressBar() {
        animateElips(binding.progressBar.progressBarGroup)
    }

    @SuppressLint("DiscouragedApi")
    private fun animateElips(progressBar: ViewGroup) {
        if (elipsIndex > 8) {
            return
        }
        val elipsId = resources.getIdentifier("elips$elipsIndex", "id", packageName)
        val elipsView = progressBar.findViewById<View>(elipsId)
        val colorsAnimator = setColorsAnimator(elipsView)
        colorsAnimator.addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                elipsIndex++
                animateElips(progressBar)
            }
        })
        colorsAnimator.start()
    }

    @SuppressLint("Recycle")
    private fun setColorsAnimator(elipsView: View): ValueAnimator {
        val colorStart = resources.getColor(R.color.light_blue)
        val colorEnd = resources.getColor(R.color.blue)

        return ValueAnimator.ofObject(ArgbEvaluator(), colorStart, colorEnd).apply {
            addUpdateListener {
                val color = it.animatedValue as Int
                val elipsDrawable = elipsView.background as? GradientDrawable
                elipsDrawable?.setColor(color)
            }
            duration = 300
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        jobLoading.cancel()
    }
}