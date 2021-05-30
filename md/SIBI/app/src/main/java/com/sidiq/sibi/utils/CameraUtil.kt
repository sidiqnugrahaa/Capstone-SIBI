package com.sidiq.sibi.utils

import android.graphics.*
import android.os.Handler
import android.os.HandlerThread
import android.util.DisplayMetrics
import android.util.Log
import android.util.Rational
import android.util.Size
import android.view.Surface
import android.view.TextureView
import android.view.WindowManager
import androidx.camera.core.*
import io.grpc.Context
import java.io.ByteArrayOutputStream

object CameraUtil {
    private val TAG = "CameraUtil"

    fun initClassify(classifier: TFLiteClassifier) =
        classifier
            .initialize()
            .addOnFailureListener { e -> Log.e(TAG, "Error in setting up the classifier.", e) }

    fun updateTransform(textureView: TextureView) {
        val matrix = Matrix()
        val centerX = textureView.width / 2f
        val centerY = textureView.height / 2f

        val rotationDegrees = when (textureView.display.rotation) {
            Surface.ROTATION_0 -> 0
            Surface.ROTATION_90 -> 90
            Surface.ROTATION_180 -> 180
            Surface.ROTATION_270 -> 270
            else -> return
        }
        matrix.postRotate(-rotationDegrees.toFloat(), centerX, centerY)
        textureView.setTransform(matrix)
    }

    fun initPreview(lensFacing : CameraX.LensFacing, textureView: TextureView,
                    windowManager: WindowManager) : Preview {
        val metrics = DisplayMetrics().also { textureView.display.getRealMetrics(it) }
        val screenSize = Size(metrics.widthPixels, metrics.heightPixels)
        val screenAspectRatio = Rational(1, 1)

        val previewConfig = PreviewConfig.Builder().apply {
            setLensFacing(lensFacing)
            setTargetResolution(screenSize)
            setTargetRotation(windowManager.defaultDisplay.rotation)
            setTargetRotation(textureView.display.rotation)
        }.build()

        return Preview(previewConfig).apply {
            setOnPreviewOutputUpdateListener {
                textureView.setSurfaceTexture(it.surfaceTexture)
                updateTransform(textureView) }
        }

    }

    // Image Analyzer Use Case
    fun ImageProxy.toBitmap(): Bitmap {
        val yBuffer = planes[0].buffer // Y
        val uBuffer = planes[1].buffer // U
        val vBuffer = planes[2].buffer // V

        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()

        val nv21 = ByteArray(ySize + uSize + vSize)

        yBuffer.get(nv21, 0, ySize)
        vBuffer.get(nv21, ySize, vSize)
        uBuffer.get(nv21, ySize + vSize, uSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, this.width, this.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 100, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    fun initAnalyzer(lensFacing: CameraX.LensFacing) : ImageAnalysis {
        val analyzerConfig = ImageAnalysisConfig.Builder().apply {
            // Use a worker thread for image analysis to prevent glitches
            val analyzerThread = HandlerThread("AnalysisThread").apply {
                start()
            }
            setLensFacing(lensFacing)
            setCallbackHandler(Handler(analyzerThread.looper))
            setImageReaderMode(ImageAnalysis.ImageReaderMode.ACQUIRE_LATEST_IMAGE)
        }.build()

        return ImageAnalysis(analyzerConfig)
    }

    // Image Capture Use Case
    fun initCapture(lensFacing: CameraX.LensFacing, textureView: TextureView) : ImageCapture {
        val metrics = DisplayMetrics().also { textureView.display.getRealMetrics(it) }
        val screenSize = Size(metrics.widthPixels, metrics.heightPixels)

        val captureConfig = ImageCaptureConfig.Builder().apply {
            setLensFacing(lensFacing)
            setTargetResolution(screenSize)
        }.build()

        return ImageCapture(captureConfig)
    }


}