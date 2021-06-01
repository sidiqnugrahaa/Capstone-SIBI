package com.sidiq.sibi.ui.maingame.practice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.sidiq.sibi.data.wrapper.Result
import com.sidiq.sibi.databinding.ActivityPracticeResultBinding
import com.sidiq.sibi.domain.model.AuthUser.Companion.toDomain
import com.sidiq.sibi.ui.FirebaseAuthViewModel
import com.sidiq.sibi.ui.maingame.GameViewModel
import com.sidiq.sibi.ui.maingame.game.GameResultActivity
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

        val user = authViewModel.checkUserLoggedIn()?.toDomain()!!

        binding.tvAnswered.text = "$score"
        binding.btnBackHome.setOnClickListener { finish() }

        gameViewModel.insertHistory(score, "Berlatih", user.userId)
        gameViewModel.result.observe(this){
            when(it){
                is Result.Success -> Toast.makeText(this, "OK", Toast.LENGTH_SHORT).show()
                is Result.Error -> Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show()
                else -> Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show()
            }
        }

        gameViewModel.spinner.observe(this){
            when(it){
                true -> binding.status.text = "Loading..."
                else -> binding.status.text = "Loaded"
            }
        }

    }
}