package com.sidiq.sibi.ui.practice

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sidiq.sibi.databinding.ActivityPracticeBinding

class PracticeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPracticeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPracticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

}