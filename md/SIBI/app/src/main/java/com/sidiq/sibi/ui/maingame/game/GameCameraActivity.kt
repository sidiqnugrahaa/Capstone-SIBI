package com.sidiq.sibi.ui.maingame.game

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.widget.Toast
import androidx.activity.viewModels
import androidx.camera.core.CameraSelector
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sidiq.sibi.databinding.ActivityGameCameraBinding
import com.sidiq.sibi.ui.maingame.GameViewModel
import com.sidiq.sibi.ui.maingame.Recognition
import com.sidiq.sibi.utils.COLOR_CORRECT
import com.sidiq.sibi.utils.CameraUtil
import com.sidiq.sibi.utils.GameTimer
import com.sidiq.sibi.utils.TIME_GAME
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executors

@AndroidEntryPoint
class GameCameraActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_WORD = "word"
    }

    private val REQUEST_CODE_PERMISSIONS = 101
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

    private val binding: ActivityGameCameraBinding by lazy {
        ActivityGameCameraBinding.inflate(layoutInflater)
    }

    private var isFinished = false
    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private val gameViewModel: GameViewModel by viewModels()

    private var word = ""
    private var spannedWord = SpannableString("")

    private var index = 0
    private lateinit var timer: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (allPermissionsGranted()) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        word = intent.getStringExtra(EXTRA_WORD)!!
        spannedWord = SpannableString(word)

        timer = GameTimer(TIME_GAME).apply {
            onTick = {
                binding.timer.text = "Sisa Waktu: $it"
            }
            onFinish = {
                gotoResult(index)
            }
        }
        timer.start()

        gameViewModel.recognition.observe(this){ recognition ->
            gradeColor(recognition)
        }

    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                //finish()
                //startActivity(intent)
                startCamera()
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

    private fun gotoResult(index: Int){
        if(!isFinished){
            isFinished = true

            val intent = Intent(this, GameResultActivity::class.java).apply {
                putExtra(GameResultActivity.EXTRA_SCORE, index)
            }
            startActivity(intent)
            timer.cancel()
            this.finish()
        }
    }

    private fun gradeColor(recognition: Recognition){
        if(recognition.label == spannedWord[index].toString()){
            when {
                index >= spannedWord.length-1 -> {
                    gotoResult(index+1)
                }
                else -> {
                    index++
                    spannedWord.setSpan(
                        COLOR_CORRECT, 0, index,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
        }
        binding.predictedTextView.text = spannedWord
        binding.debugText.text = "$index - ${recognition.label}"
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