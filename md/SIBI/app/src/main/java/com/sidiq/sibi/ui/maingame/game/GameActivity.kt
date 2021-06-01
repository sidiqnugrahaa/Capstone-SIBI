package com.sidiq.sibi.ui.maingame.game

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sidiq.sibi.databinding.ActivityGameBinding
import com.sidiq.sibi.ui.maingame.practice.PracticeCameraActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val string = "LAVCV"

        binding.tvWord.text = string

        binding.btnStartGame.setOnClickListener {
            val intent = Intent(this, GameCameraActivity::class.java).apply {
                putExtra(GameCameraActivity.EXTRA_WORD, string)
            }
            startActivity(intent)
            this.finish()
        }
    }

}