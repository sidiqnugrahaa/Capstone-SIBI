package com.sidiq.sibi.ui.contribute

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sidiq.sibi.databinding.ActivityContributeCameraBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContributeCameraActivity : AppCompatActivity() {
    private val binding: ActivityContributeCameraBinding by lazy {
        ActivityContributeCameraBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }
}