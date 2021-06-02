package com.sidiq.sibi.ui.contribute

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import com.sidiq.sibi.data.wrapper.UploadResult
import com.sidiq.sibi.databinding.ActivityContributeResultBinding
import com.sidiq.sibi.domain.model.AuthUser.Companion.toDomain
import com.sidiq.sibi.domain.model.Contrib
import com.sidiq.sibi.domain.model.Word
import com.sidiq.sibi.ui.FirebaseAuthViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class ContributeResultActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_CONTRIB = "contrib"
    }

    private val binding: ActivityContributeResultBinding by lazy {
        ActivityContributeResultBinding.inflate(layoutInflater)
    }

    private val contributeViewModel : ContributeViewModel by viewModels()
    private val authViewModel : FirebaseAuthViewModel by viewModels()

    private var word = Word()

    @ExperimentalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        word = intent.getParcelableExtra(EXTRA_CONTRIB)!!
        val userId = authViewModel.checkUserLoggedIn()!!.toDomain()!!.userId
        word.contrib!!.userId = userId

        contributeViewModel.insertContrib(word)
        contributeViewModel.result.observe(this){ result ->
            when(result){
                is UploadResult.InProgress -> binding.status.text = "Loading..."
                is UploadResult.Paused -> binding.status.text = "Paused"
                is UploadResult.Complete.Success -> binding.status.text = "Success"
                is UploadResult.Complete.Cancelled -> binding.status.text = "Cancelled"
                is UploadResult.Complete.Failed -> binding.status.text = "Failed"
            }
        }

        binding.btnBackHome.setOnClickListener { finish() }
    }
}