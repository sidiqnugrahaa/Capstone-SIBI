package com.sidiq.sibi.utils

import android.content.Context
import android.util.Log
import android.util.Size
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import java.io.File
import java.util.concurrent.Executor

object CameraUtil {
    private const val TAG = "CameraUtil"

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

    private val detectorOption = ObjectDetector.ObjectDetectorOptions.builder()
        .setMaxResults(1)
        .setScoreThreshold(CONFIDENCE_THRESHOLD)
        .build()

    private fun initDetector(context: Context): ObjectDetector = ObjectDetector.createFromFileAndOptions(
        context,
        MODEL_PATH,
        detectorOption
    )

    fun cameraSelector(cameraProvider: ProcessCameraProvider, type: CameraSelector): CameraSelector {
        val reverse = when(type){
            CameraSelector.DEFAULT_BACK_CAMERA -> CameraSelector.DEFAULT_FRONT_CAMERA
            else -> CameraSelector.DEFAULT_BACK_CAMERA
        }
        return if(cameraProvider.hasCamera(type)) type else reverse
    }


    fun startAnalyze(ctx: Context,
                     cameraExecutor: Executor,
                     type: CameraSelector,
                     lifecycleOwner: LifecycleOwner,
                     previewView: PreviewView,
                     listener: ObjectListener){
        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = initPreview()

            val detector = initDetector(ctx)

            val imageAnalyzer = initAnalysis()
                .also { analysisUseCase: ImageAnalysis ->
                    analysisUseCase.setAnalyzer(
                        cameraExecutor, ObjectAnalyzer(ctx, detector, listener))
                }

            val cameraSelector = cameraSelector(cameraProvider, type)

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
                    type: CameraSelector,
                    videoCapture: VideoCapture,
                    previewView: PreviewView){

        val cameraProviderFuture = ProcessCameraProvider.getInstance(ctx)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = initPreview()

            val cameraSelector = cameraSelector(cameraProvider, type)

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