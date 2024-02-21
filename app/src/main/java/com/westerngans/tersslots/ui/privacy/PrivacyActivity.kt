package com.westerngans.tersslots.ui.privacy

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.westerngans.tersslots.R
import com.westerngans.tersslots.databinding.ActivityPrivacyBinding
import com.westerngans.tersslots.ui.menu.MenuActivity
import com.westerngans.tersslots.utils.HideUI

class PrivacyActivity : AppCompatActivity() {
    private val binding by lazy { ActivityPrivacyBinding.inflate(layoutInflater) }
    private var linkPrivacy: String = "https://www.google.com" //TODO change on release
    private lateinit var startIntent: Intent
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        HideUI.hideUI(this)
        clickAccept()
    }

    private fun clickAccept() {
        val animationClick = AnimationUtils.loadAnimation(this, R.anim.scale)
        val btnPrivacyListener = View.OnClickListener {
            binding.btnAccept.startAnimation(animationClick)
            startMenu()
        }
        binding.btnAccept.setOnClickListener(btnPrivacyListener)
        binding.textPrivacy.setOnClickListener {
            binding.textPrivacy.startAnimation(animationClick)
            startIntentPrivacy()
        }
    }

    private fun startMenu() {
        startIntent = Intent(this@PrivacyActivity, MenuActivity::class.java)
        startActivity(startIntent)
        finish()
    }

    private fun startIntentPrivacy() {
        this.let {
            startIntent = Intent(Intent.ACTION_VIEW, Uri.parse(linkPrivacy))
            startActivity(startIntent)
        }
    }

    @Deprecated(
        "Deprecated in Java",
        ReplaceWith("super.onBackPressed()", "androidx.appcompat.app.AppCompatActivity")
    )
    override fun onBackPressed() {
        super.onBackPressed()
    }
}