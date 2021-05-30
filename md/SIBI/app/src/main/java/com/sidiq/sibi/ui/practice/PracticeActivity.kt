package com.sidiq.sibi.ui.practice

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.sidiq.sibi.databinding.ActivityPracticeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PracticeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPracticeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPracticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnFinishPractice.setOnClickListener {
            startActivity(Intent(this, PracticeCameraActivity::class.java))
        }
    }

}