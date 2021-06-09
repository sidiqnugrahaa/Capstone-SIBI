package com.sidiq.sibi.ui.contribute

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DataSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.util.Util
import com.sidiq.sibi.data.wrapper.Resource
import com.sidiq.sibi.databinding.FragmentContributeBinding
import com.sidiq.sibi.domain.model.Word
import com.sidiq.sibi.ui.FunctionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContributeFragment : Fragment(), Player.Listener  {
    private lateinit var binding: FragmentContributeBinding
    private val functionViewModel: FunctionViewModel by activityViewModels()
    private var word = Word()

    private var playerWhenReady = true
    private var playbackPosition: Long = 0
    private lateinit var exoPlayer: SimpleExoPlayer
    private lateinit var mediaDataSourceFactory: DataSource.Factory


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentContributeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (activity != null) {

            functionViewModel.getRandomWord()
            initView()

        }
    }

    private fun initView(){
        functionViewModel.word.observe(viewLifecycleOwner){
            when(it){
                is Resource.Loading -> { binding.btnStartContrib.isEnabled = false }

                is Resource.Failure -> {
                    binding.btnStartContrib.isEnabled = false
                    Toast.makeText(context, "Gagal Ambil Data", Toast.LENGTH_SHORT).show()
                }

                is Resource.Success -> {
                    word = it.data
                    binding.btnStartContrib.isEnabled = true
                    initPlayer()
                    binding.btnStartContrib.setOnClickListener {
                        val intent = Intent(activity, ContributeCameraActivity::class.java).apply {
                            putExtra(ContributeCameraActivity.EXTRA_WORD, word)
                        }
                        startActivity(intent)
                    }
                }
                else -> {}
            }
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
        val uri = Uri.parse(word.link)
        exoPlayer = SimpleExoPlayer.Builder(requireContext()).build()
        mediaDataSourceFactory = DefaultDataSourceFactory(requireContext(), Util.getUserAgent(requireContext(), "mediaPlayerSample"))
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

}