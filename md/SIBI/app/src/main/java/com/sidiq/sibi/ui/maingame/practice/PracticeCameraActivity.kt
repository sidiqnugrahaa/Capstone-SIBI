package com.sidiq.sibi.ui.maingame.practice

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.Toast
import androidx.activity.viewModels
import androidx.camera.core.CameraSelector
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.bumptech.glide.Glide
import com.sidiq.sibi.databinding.ActivityPracticeCameraBinding
import com.sidiq.sibi.domain.model.Alphabet
import com.sidiq.sibi.domain.model.LearningData
import com.sidiq.sibi.ui.maingame.Recognition
import com.sidiq.sibi.ui.maingame.GameViewModel
import com.sidiq.sibi.utils.CameraUtil
import com.sidiq.sibi.utils.TIME_PRACTICE
import com.sidiq.sibi.utils.TIME_PRACTICE_ADD
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executors

@AndroidEntryPoint
class PracticeCameraActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_ALPHABET = "alphabet"
    }

    private val REQUEST_CODE_PERMISSIONS = 101
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)


    private val binding: ActivityPracticeCameraBinding by lazy {
        ActivityPracticeCameraBinding.inflate(layoutInflater)
    }

    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private val gameViewModel: GameViewModel by viewModels()

    private var isFinished = false
    private var score = 0
    private var alphabet : Alphabet? = null
    private lateinit var timer: CountDownTimer
    private var remainingTime: Long = 0


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        alphabet = intent.getParcelableExtra(EXTRA_ALPHABET)!!

        timer = createTimer(TIME_PRACTICE)
        timer.start()

        gameViewModel.recognition.observe(this){ recognition ->
            gradeColor(recognition)
        }

    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
        grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                finish()
                startActivity(intent)
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun startCamera() {
        CameraUtil.startAnalyze(ctx = this, cameraExecutor = cameraExecutor,
            type = CameraSelector.DEFAULT_BACK_CAMERA,
            lifecycleOwner = this, previewView = binding.viewFinder){ item ->
                gameViewModel.updateData(item)
            }
    }

    private fun refreshTimer() {
        timer.cancel()
        timer = createTimer(remainingTime + TIME_PRACTICE_ADD)
        timer.start()
    }

    private fun gotoResult(index: Int){
        if(!isFinished){
            isFinished = true

            val intent = Intent(this, PracticeResultActivity::class.java).apply {
                putExtra(PracticeResultActivity.EXTRA_SCORE, index)
            }
            startActivity(intent)
            timer.cancel()
            this.finish()
        }
    }

    private fun gradeColor(recognition: Recognition){
        if(recognition.label == alphabet?.alphabet.toString()){
            score++
            alphabet = LearningData.listData.random()
            refreshTimer()
        }

        Glide.with(this)
            .load(alphabet?.icon)
            .into(binding.imageAlphabet)

        //binding.debugText.text = "$score - ${recognition.label}"

    }


    private fun createTimer(seconds: Long) : CountDownTimer {
        return object : CountDownTimer(seconds*1000, 1000) {
            override fun onTick(miliFinished: Long) {
                remainingTime = miliFinished/1000
                binding.timer.text = "Sisa Waktu: ${miliFinished / 1000}"
            }

            override fun onFinish() {
                binding.timer.text = "Waktu Habis"
                gotoResult(score)
            }
        }
    }


    private fun allPermissionsGranted(): Boolean {

        for (permission in REQUIRED_PERMISSIONS) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    permission
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                return false
            }
        }
        return true
    }

    override fun onDestroy() {
        timer.cancel()
        super.onDestroy()
    }

}