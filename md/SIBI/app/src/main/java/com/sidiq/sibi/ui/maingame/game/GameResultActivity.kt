package com.sidiq.sibi.ui.maingame.game

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import com.sidiq.sibi.R
import com.sidiq.sibi.databinding.ActivityGameResultBinding
import com.sidiq.sibi.domain.model.AuthUser.Companion.toDomain
import com.sidiq.sibi.ui.FirebaseAuthViewModel
import com.sidiq.sibi.ui.maingame.GameViewModel
import com.sidiq.sibi.utils.SCORE_MULTIPLIER
import kotlinx.coroutines.ExperimentalCoroutinesApi
import com.sidiq.sibi.data.wrapper.Result
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameResultActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_SCORE = "score"
    }

    private val binding: ActivityGameResultBinding by lazy {
        ActivityGameResultBinding.inflate(layoutInflater)
    }
    private val authViewModel : FirebaseAuthViewModel by viewModels()
    private val gameViewModel : GameViewModel by viewModels()
    private var score: Int = 0

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        score = intent.getIntExtra(EXTRA_SCORE, 0)

        val user = authViewModel.checkUserLoggedIn()?.toDomain()!!

        score *= SCORE_MULTIPLIER
        binding.tvScore.text = "$score"

        binding.btnBackHome.setOnClickListener { finish() }
        binding.btnCheckLeaderboard.setOnClickListener {
            //TODO: Navigate To Leaderboard
            finish()
        }

        gameViewModel.insertHistory(score, "Bermain", user.userId)
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