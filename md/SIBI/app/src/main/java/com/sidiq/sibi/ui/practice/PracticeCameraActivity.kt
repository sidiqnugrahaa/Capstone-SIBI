package com.sidiq.sibi.ui.practice

import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.camera.core.CameraX
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.sidiq.sibi.databinding.ActivityPracticeCameraBinding
import com.sidiq.sibi.utils.CameraUtil
import com.sidiq.sibi.utils.CameraUtil.toBitmap
import com.sidiq.sibi.utils.TFLiteClassifier
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PracticeCameraActivity : AppCompatActivity() {
    var lensFacing = CameraX.LensFacing.BACK

    private var pauseAnalysis = false
    private var imageRotationDegrees: Int = 0

    private val REQUEST_CODE_PERMISSIONS = 101
    private val REQUIRED_PERMISSIONS = arrayOf("android.permission.CAMERA")
    private var tfLiteClassifier: TFLiteClassifier = TFLiteClassifier(this)

    private var allLabel: String = ""
    private var label: String = ""

    private val binding: ActivityPracticeCameraBinding by lazy {
        ActivityPracticeCameraBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        if (allPermissionsGranted()) {
            binding.textureView.apply {
                post { startCamera() }
                addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ ->
                    CameraUtil.updateTransform(this)
                }
            }
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS);
        }

        tfLiteClassifier
            .initialize()
            .addOnSuccessListener { }
            .addOnFailureListener { e -> Log.e("CAMERA", "Error in setting up the classifier.", e) }

        binding.cameraCaptureButton.setOnClickListener {

            it.isEnabled = false

            if (pauseAnalysis) {
                pauseAnalysis = false
            } else {
                allLabel += label
                binding.predictedTextView.text = allLabel
            }
            it.isEnabled = true
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

    private fun startCamera(){
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
                    binding.predictedTextView.text = resultText

//                    if(resultText == "space") {
//                        binding.predictedTextView.text = resultText
//                        pauseAnalysis = true
//                    }
                }
                .addOnFailureListener { error ->  }
        }

        CameraX.unbindAll()

        CameraX.bindToLifecycle(this, preview, imageAnalysis)
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
        tfLiteClassifier.close()
        CameraX.unbindAll()
        super.onDestroy()
    }
}