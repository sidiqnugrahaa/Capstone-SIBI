package com.sidiq.sibi.ui.maingame.game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sidiq.sibi.databinding.ActivityGameCameraBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameCameraActivity : AppCompatActivity() {
    private val binding: ActivityGameCameraBinding by lazy {
        ActivityGameCameraBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}