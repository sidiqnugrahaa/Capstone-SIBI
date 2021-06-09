package com.sidiq.sibi.ui.maingame.practice

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.sidiq.sibi.data.wrapper.Result
import com.sidiq.sibi.databinding.ActivityPracticeResultBinding
import com.sidiq.sibi.ui.FirebaseAuthViewModel
import com.sidiq.sibi.ui.maingame.GameViewModel
import com.sidiq.sibi.utils.SCORE_MULTIPLIER
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class PracticeResultActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_SCORE = "score"
    }

    private val binding: ActivityPracticeResultBinding by lazy {
        ActivityPracticeResultBinding.inflate(layoutInflater)
    }
    private val authViewModel : FirebaseAuthViewModel by viewModels()
    private val gameViewModel : GameViewModel by viewModels()
    private var score: Int = 0

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        score = intent.getIntExtra(EXTRA_SCORE, 0)
        score *= SCORE_MULTIPLIER

        val user = authViewModel.checkUserLogin()!!

        binding.tvAnswered.text = "$score"
        binding.btnBackHome.setOnClickListener { finish() }

        gameViewModel.insertHistory(score, "Berlatih", user.userId)
        gameViewModel.result.observe(this){
            when(it){
                is Result.Success -> Toast.makeText(this, "Berhasil Mengirim Data", Toast.LENGTH_SHORT).show()
                is Result.Error -> Toast.makeText(this, "Gagal Mengirim Data", Toast.LENGTH_SHORT).show()
                else -> Toast.makeText(this, "Gagal Mengirim Data", Toast.LENGTH_SHORT).show()
            }
        }

        gameViewModel.spinner.observe(this){
            when(it){
                true -> binding.loading.visibility = View.VISIBLE
                else -> binding.loading.visibility = View.GONE
            }
        }

    }
}