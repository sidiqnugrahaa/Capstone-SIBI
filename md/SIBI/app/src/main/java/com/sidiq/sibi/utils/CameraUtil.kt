package com.sidiq.sibi.utils

import android.content.Context
import android.util.Log
import android.util.Size
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import java.io.File
import java.util.concurrent.Executor

object CameraUtil {
    private val TAG = "CameraUtil"

    private fun initPreview() = Preview.Builder()
        .build()

    private fun initAnalysis() = ImageAnalysis.Builder()
        .setTargetResolution(Size(INPUT_IMAGE_SIZE, INPUT_IMAGE_SIZE))
        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
        .build()

    fun initVideo() = VideoCapture.Builder()
        .build()
    fun initVideoOutput(file: File) = VideoCapture.OutputFileOptions.Builder(file)
        .build()

    fun cameraSelector(cameraProvider: ProcessCameraProvider) =
        if (cameraProvider.hasCamera(CameraSelector.DEFAULT_FRONT_CAMERA))
            CameraSelector.DEFAULT_FRONT_CAMERA else CameraSelector.DEFAULT_BACK_CAMERA

    fun startAnalyze(ctx: Context,
                     cameraExecutor: Executor,
                     lifecycleOwner: LifecycleOwner,
                     previewView: PreviewView,
                     listener: RecognitionListener){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = initPreview()

            val imageAnalyzer = initAnalysis()
                .also { analysisUseCase: ImageAnalysis ->
                    analysisUseCase.setAnalyzer(cameraExecutor, SibiClassifier(ctx, listener))
                }

            val cameraSelector = cameraSelector(cameraProvider)

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, imageAnalyzer)
                preview.setSurfaceProvider(previewView.surfaceProvider)

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(ctx))

    }

    fun startRecord(ctx: Context,
                     lifecycleOwner: LifecycleOwner,
                     videoCapture: VideoCapture,
                     previewView: PreviewView){

        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = initPreview()

            val cameraSelector = cameraSelector(cameraProvider)

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    lifecycleOwner, cameraSelector, preview, videoCapture)
                preview.setSurfaceProvider(previewView.surfaceProvider)

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(ctx))

    }

}