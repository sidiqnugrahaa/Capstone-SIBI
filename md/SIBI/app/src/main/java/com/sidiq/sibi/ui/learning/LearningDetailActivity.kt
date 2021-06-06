package com.sidiq.sibi.ui.learning

import android.net.Uri
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.sidiq.sibi.databinding.ActivityDetailLearnBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LearningDetailActivity : AppCompatActivity(), Player.Listener {
    private lateinit var binding: ActivityDetailLearnBinding

    private lateinit var exoPlayer: SimpleExoPlayer
    private lateinit var mediaDataSourceFactory: DataSource.Factory

    private var playerWhenReady = true
    private var playbackPosition: Long = 0
    private var imgUrl : String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailLearnBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val extras = intent.extras
        if (extras != null) {
            val icon = extras.getInt(EXTRA_IMG)
            imgUrl = IMG_URL + extras.getString(EXTRA_URL)

            Glide.with(this)
                .load(icon)
                .apply(RequestOptions())
                .into(binding.alphabetIcon)

            initPlayer()
        }

    }

    private fun initPlayer() {
        preparePlayer()
        exoPlayer.seekTo(playbackPosition)
        exoPlayer.playWhenReady = playerWhenReady
        exoPlayer.addListener(this)
        binding.exoplayerView.player = exoPlayer
    }

    override fun onStart() {
        super.onStart()
        if (Util.SDK_INT >= 24) initPlayer()
    }

    override fun onStop() {
        super.onStop()
        if (Util.SDK_INT >= 24) releasePlayer()
    }

    private fun releasePlayer() {
        playerWhenReady = exoPlayer.playWhenReady
        playbackPosition = exoPlayer.currentPosition

        exoPlayer.removeListener(this)
        exoPlayer.release()
        binding.exoplayerView.player = null
    }

    private fun preparePlayer() {
        val uri = Uri.parse(imgUrl)
        exoPlayer = SimpleExoPlayer.Builder(this).build()
        mediaDataSourceFactory = DefaultDataSourceFactory(this, Util.getUserAgent(this, "mediaPlayerSample"))
        val mediaSource = ProgressiveMediaSource.Factory(mediaDataSourceFactory)
            .createMediaSource(MediaItem.fromUri(uri))
        exoPlayer.setMediaSource(mediaSource, false)
        exoPlayer.prepare()
    }

    override fun onPlayerStateChanged(playWhenReady: Boolean, playbackState: Int) {
        if (playbackState == Player.STATE_BUFFERING)
            binding.progressBar.visibility = View.VISIBLE
        else if (playbackState == Player.STATE_READY || playbackState == Player.STATE_ENDED)
            binding.progressBar.visibility = View.INVISIBLE
    }

    override fun onDestroy() {
        releasePlayer()
        super.onDestroy()
    }

    companion object {
        const val EXTRA_IMG = "extra_img"
        const val EXTRA_URL = "extra_url"
        const val IMG_URL = "https://pmpk.kemdikbud.go.id/sibi/SIBI/katadasar/"
    }
}