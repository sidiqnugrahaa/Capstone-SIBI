package com.sidiq.sibi.ui.contribute

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.sidiq.sibi.databinding.ActivityContributeResultBinding
import com.sidiq.sibi.domain.model.Contrib
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContributeResultActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_CONTRIB = "contrib"
    }

    private val binding: ActivityContributeResultBinding by lazy {
        ActivityContributeResultBinding.inflate(layoutInflater)
    }

    private var contrib = Contrib()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        contrib = intent.getParcelableExtra(EXTRA_CONTRIB)!!

        binding.status.text = contrib.fileUri
        binding.btnBackHome.setOnClickListener { finish() }
    }
}