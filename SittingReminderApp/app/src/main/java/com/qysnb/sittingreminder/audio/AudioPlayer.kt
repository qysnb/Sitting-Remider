package com.qysnb.sittingreminder.audio

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.net.Uri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.coroutineContext

class AudioPlayer {

    private var mediaPlayer: MediaPlayer? = null
    private var playJob: Job? = null

    suspend fun play(context: Context, uri: Uri, volume: Float = 1f, durationMs: Long = 0L) {
        playJob?.cancel()
        playJob = coroutineContext[Job]
        withContext(Dispatchers.Main) {
            release()
            val player = MediaPlayer().apply {
                setAudioAttributes(
                    AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build()
                )
                setDataSource(context, uri)
            }
            suspendCancellableCoroutine<Unit> { cont ->
                player.setOnPreparedListener {
                    player.setVolume(volume, volume)
                    player.start()
                    cont.resume(Unit) { }
                }
                player.setOnErrorListener { _, _, _ ->
                    cont.resume(Unit) { }
                    false
                }
                player.prepareAsync()
            }
            mediaPlayer = player
        }
        if (durationMs > 0 && coroutineContext.isActive) {
            delay(durationMs)
            if (coroutineContext.isActive) release()
        }
    }

    fun stop() {
        mediaPlayer?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        mediaPlayer = null
    }

    fun release() {
        mediaPlayer?.let {
            if (it.isPlaying) it.stop()
            it.release()
        }
        mediaPlayer = null
    }

    fun isPlaying(): Boolean = mediaPlayer?.isPlaying == true
}
