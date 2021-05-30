package com.sidiq.sibi.ui.learning

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sidiq.sibi.databinding.ActivityDetailLearnBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LearningDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailLearnBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailLearnBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}