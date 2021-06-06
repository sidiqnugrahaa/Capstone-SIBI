package com.sidiq.sibi.ui.contribute

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.text.SpannableString
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.camera.core.VideoCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Timestamp
import com.sidiq.sibi.R
import com.sidiq.sibi.databinding.ActivityContributeCameraBinding
import com.sidiq.sibi.domain.model.Contrib
import com.sidiq.sibi.domain.model.Word
import com.sidiq.sibi.ui.maingame.game.GameCameraActivity
import com.sidiq.sibi.utils.CameraUtil
import com.sidiq.sibi.utils.GameTimer
import com.sidiq.sibi.utils.TIME_CONTRIB
import com.sidiq.sibi.utils.TIME_GAME
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors

@AndroidEntryPoint
class ContributeCameraActivity : AppCompatActivity() {
    companion object {
        const val EXTRA_WORD = "word"
        const val TAG = "CONTRIB"
    }

    private val binding: ActivityContributeCameraBinding by lazy {
        ActivityContributeCameraBinding.inflate(layoutInflater)
    }
    private val REQUEST_CODE_PERMISSIONS = 101
    private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO)

    private val cameraExecutor = Executors.newSingleThreadExecutor()
    private var isFinished = false
    private var outputDirectory : File? = null
    private lateinit var videoCapture : VideoCapture
    private lateinit var timer: CountDownTimer
    private var word = Word()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        timer = GameTimer(TIME_CONTRIB).apply {
            onTick = {
                binding.timer.text = "$it"
            }
            onFinish = { stopRecording() }
        }


        if (allPermissionsGranted()) {
            outputDirectory = getMediaOutputDirectory()
            startCamera()
            binding.cameraCaptureButton.setOnClickListener{
                timer.start()
                it.visibility = View.GONE
                startRecording()
            }
        } else {
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }

        word = intent.getParcelableExtra(GameCameraActivity.EXTRA_WORD)!!


    }
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>,
                                            grantResults: IntArray) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                outputDirectory = getMediaOutputDirectory()

                startCamera()

                binding.cameraCaptureButton.setOnClickListener{
                    timer.start()
                    it.visibility = View.GONE
                    startRecording()
                }
            } else {
                Toast.makeText(this, "Permissions not granted by the user.", Toast.LENGTH_SHORT)
                    .show()
                finish()
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    private fun startCamera() {
        videoCapture = CameraUtil.initVideo()
        CameraUtil.startRecord(this, this, videoCapture, binding.viewFinder)
    }

    private fun getMediaOutputDirectory(): File {
        val mediaDir = externalMediaDirs.firstOrNull()?.let {
            File(it, resources.getString(R.string.app_name)).apply { mkdirs() } }
        return if (mediaDir != null && mediaDir.exists())
            mediaDir else filesDir
    }

    private fun goToResult(word: Word, savedUri: Uri){
        if(!isFinished){
            isFinished = true

            val data = Word(word.word.lowercase(), word.link, Contrib(fileUri = savedUri.toString(),
                timestamp = Timestamp.now()))
            val intent = Intent(this, ContributeResultActivity::class.java).apply {
                putExtra(ContributeResultActivity.EXTRA_CONTRIB, data)
            }
            startActivity(intent)
            this.finish()
        }
    }

    @SuppressLint("RestrictedApi", "MissingPermission")
    private fun startRecording() {
        val videoFile = File(
            outputDirectory,
            SimpleDateFormat("yyyy-MM-dd-HH-mm-ss-SSS", Locale.US
            ).format(System.currentTimeMillis()) + ".mp4")
        val outputOptions = CameraUtil.initVideoOutput(videoFile)

        videoCapture.startRecording(outputOptions, cameraExecutor, object: VideoCapture.OnVideoSavedCallback {
            override fun onError(videoCaptureError: Int, message: String, cause: Throwable?) {
                Log.e(TAG, "Video capture failed: $message")
            }

            override fun onVideoSaved(outputFileResults: VideoCapture.OutputFileResults) {
                val savedUri = Uri.fromFile(videoFile)
                val msg = "Video capture succeeded: $savedUri"
                Log.d(TAG, msg)

                goToResult(word, savedUri)
            }
        })
    }

    @SuppressLint("RestrictedApi")
    private fun stopRecording() {
        videoCapture.stopRecording()
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