package com.sidiq.sibi.ui.maingame.game

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.sidiq.sibi.data.wrapper.Resource
import com.sidiq.sibi.databinding.ActivityGameBinding
import com.sidiq.sibi.ui.FunctionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class GameActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameBinding
    private val functionViewModel: FunctionViewModel by viewModels()
    private var string = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        functionViewModel.getRandomWord()
        initView()
    }

    private fun initView(){
        functionViewModel.word.observe(this){
            when(it){
                is Resource.Loading -> {
                    binding.loading.visibility = View.VISIBLE
                    binding.btnStartGame.isEnabled = false
                }

                is Resource.Failure -> {
                    binding.loading.visibility = View.GONE
                    binding.btnStartGame.isEnabled = false
                    Toast.makeText(this, "Gagal Ambil Data", Toast.LENGTH_SHORT).show()
                }

                is Resource.Success -> {
                    binding.loading.visibility = View.GONE
                    string = it.data.word.uppercase()
                    binding.tvWord.text = string
                    binding.btnStartGame.isEnabled = true
                    binding.btnStartGame.setOnClickListener {
                        val intent = Intent(this, GameCameraActivity::class.java).apply {
                            putExtra(GameCameraActivity.EXTRA_WORD, string)
                        }
                        startActivity(intent)
                        finish()
                    }
                }
                else -> {}
            }
        }
    }

}