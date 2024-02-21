package com.westerngans.tersslots.ui.scene.utils

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class SoundManager(private val context: Context) {
    private var mediaPlayer: MediaPlayer? = null
    private var soundIdMap: MutableMap<String, Int> = mutableMapOf()
    private val scope = CoroutineScope(Dispatchers.IO)

    init {
        initializeMediaPlayer()
    }

    private fun initializeMediaPlayer() {
        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                    .build()
            )
        }
    }

    fun loadSound(context: Context, soundName: String, resId: Int) {
        mediaPlayer?.reset()
        mediaPlayer = MediaPlayer.create(context, resId)
        soundIdMap[soundName] = resId
    }

    fun playSound(soundName: String, loop: Boolean = false) {
        val resId = soundIdMap[soundName]
        if (resId != null) {
            mediaPlayer?.reset()
            mediaPlayer = MediaPlayer.create(context, resId)
            mediaPlayer?.isLooping = loop
            scope.launch {
                mediaPlayer?.start()
            }
        }
    }

    fun pause() {
        if (mediaPlayer?.isPlaying == true) {
            mediaPlayer?.pause()
        }
    }

    fun resume() {
        mediaPlayer?.start()
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
        scope.cancel()
    }
}