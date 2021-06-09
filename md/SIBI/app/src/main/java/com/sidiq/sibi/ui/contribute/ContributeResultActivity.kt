package com.sidiq.sibi.ui.contribute

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.sidiq.sibi.data.wrapper.UploadResult
import com.sidiq.sibi.databinding.ActivityContributeResultBinding
import com.sidiq.sibi.domain.model.AuthUser.Companion.toDomain
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
                is UploadResult.InProgress -> {
                    binding.loading.visibility = View.VISIBLE
                    binding.btnBackHome.isEnabled = false
                }
                is UploadResult.Paused -> {
                    binding.loading.visibility = View.GONE
                    binding.btnBackHome.isEnabled = true
                    Toast.makeText(this, "Upload Paused", Toast.LENGTH_SHORT).show()
                }
                is UploadResult.Complete.Success -> {
                    binding.loading.visibility = View.GONE
                    binding.btnBackHome.isEnabled = true
                    Toast.makeText(this, "Berhasil Kontribusi", Toast.LENGTH_SHORT).show()
                }
                is UploadResult.Complete.Cancelled -> {
                    binding.loading.visibility = View.GONE
                    binding.btnBackHome.isEnabled = true
                    Toast.makeText(this, "Upload Dihentikan", Toast.LENGTH_SHORT).show()
                }
                is UploadResult.Complete.Failed -> {
                    binding.loading.visibility = View.GONE
                    binding.btnBackHome.isEnabled = true
                    result.error.message?.let { Log.e("UPLOAD", it) }
                    Toast.makeText(this, "Gagal Mengupload", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.btnBackHome.setOnClickListener { finish() }
    }
}