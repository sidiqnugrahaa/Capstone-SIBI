package com.sidiq.sibi.ui.game

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sidiq.sibi.databinding.ActivityGameBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

}