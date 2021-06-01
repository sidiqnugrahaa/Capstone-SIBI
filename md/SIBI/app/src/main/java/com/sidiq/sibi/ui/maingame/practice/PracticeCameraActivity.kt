package com.sidiq.sibi.ui.maingame.practice

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.util.Size
import android.widget.Toast
import androidx.activity.viewModels
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sidiq.sibi.databinding.ActivityPracticeCameraBinding
import com.sidiq.sibi.ui.maingame.Recognition
import com.sidiq.sibi.ui.maingame.RecognitionListViewModel
import com.sidiq.sibi.utils.COLOR_CORRECT
import com.sidiq.sibi.utils.CameraUtil
import com.sidiq.sibi.utils.INPUT_IMAGE_SIZE
import com.sidiq.sibi.utils.SibiClassifier
import dagger.hilt.android.AndroidEntryPoint
import java.util.concurrent.Executors

@AndroidEntryPoint
class PracticeCameraActivity : AppCompatActivity() {

    private val cameraExecutor = Executors.newSingleThreadExecutor()

    private var pauseAnalysis = false

    private val REQUEST_CODE_PERMISSIONS = 101
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)


    private val binding: ActivityPracticeCameraBinding by lazy {
        ActivityPracticeCameraBinding.inflate(layoutInflater)
    }
    private val recogViewModel: RecognitionListViewModel by viewModels()

    private val word = "LAVCO"
    private var spannableString = SpannableString(word)

    private var index = 0
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

        timer = createTimer(15)
        timer.start()

        recogViewModel.recognition.observe(this){ recognition ->
            gradeColor(recognition)
        }



    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
        grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
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
        CameraUtil.startCamera(ctx = this, cameraExecutor = cameraExecutor,
            lifecycleOwner = this, previewView = binding.viewFinder){ item ->
                recogViewModel.updateData(item)
            }
    }

    private fun gradeColor(recognition: Recognition){
        if(recognition.label == spannableString[index].toString()){
            when {
                index >= spannableString.length-1 -> {
                    spannableString = SpannableString("MENANG")
                }
                else -> {
                    index++
                    spannableString.setSpan(COLOR_CORRECT, 0, index,
                        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
            }
        }
        binding.predictedTextView.text = spannableString
        binding.debugText.text = "$index - ${recognition.label}"


    }

    // We'll Use This @ Exercise Mode
    private fun refreshTimer() {
        timer.cancel()
        timer = createTimer(remainingTime + 15)
        timer.start()
    }

    private fun createTimer(seconds: Long) : CountDownTimer {
        return object : CountDownTimer(seconds*1000, 1000) {
            override fun onTick(miliFinished: Long) {
                remainingTime = miliFinished/1000
                binding.timer.text = "Sisa Waktu: ${miliFinished / 1000}"
            }

            override fun onFinish() {
                binding.timer.text = "Waktu Habis"
            }
        }
    }


    /*
    private fun startCamera_b(){
        val preview = CameraUtil.initPreview(lensFacing, binding.textureView, windowManager)
        val imageAnalysis = CameraUtil.initAnalyzer(lensFacing)
        imageAnalysis.analyzer = ImageAnalysis.Analyzer { image: ImageProxy, rotationDegrees: Int ->
            if (pauseAnalysis) {
                return@Analyzer
            }

            val bitmapBuffer = image.toBitmap()

            tfLiteClassifier
                .classifyAsync(bitmapBuffer)
                .addOnSuccessListener { resultText ->
                    label = resultText

                    if(resultText == "space") {
                        binding.predictedTextView.text = resultText
                        pauseAnalysis = true
                    }
                }
                .addOnFailureListener { error ->  }
        }

        CameraX.unbindAll()

        CameraX.bindToLifecycle(this, preview, imageAnalysis)
    }
    */
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