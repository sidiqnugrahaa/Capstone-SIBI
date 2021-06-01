package com.sidiq.sibi.ui.maingame.practice

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.sidiq.sibi.databinding.ActivityPracticeBinding
import com.sidiq.sibi.domain.model.LearningData
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PracticeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPracticeBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPracticeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val alphabet = LearningData.listData.random()

        Glide.with(this)
            .load(alphabet.icon)
            .into(binding.imageAlphabet)

        binding.btnStartPractice.setOnClickListener {
            val intent = Intent(this, PracticeCameraActivity::class.java).apply {
                putExtra(PracticeCameraActivity.EXTRA_ALPHABET, alphabet)
            }
            startActivity(intent)
            this.finish()
        }
    }

}