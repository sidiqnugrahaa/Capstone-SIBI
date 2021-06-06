package com.sidiq.sibi.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.media.Image
import android.util.Log
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.sidiq.sibi.ui.maingame.Recognition
import com.sidiq.sibi.ui.maingame.Recognition.Companion.toRecognition
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.task.vision.detector.Detection
import org.tensorflow.lite.task.vision.detector.ObjectDetector
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer

typealias ObjectListener = (recognition: Recognition) -> Unit

class ObjectAnalyzer(context: Context,
                     private val detector: ObjectDetector,
                     private val listener: ObjectListener) : ImageAnalysis.Analyzer {

    private val yuvToRgbConverter = YuvToRgbConverter(context)
    private lateinit var bitmapBuffer: Bitmap
    private lateinit var rotationMatrix: Matrix

    @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
    private fun toBitmap(imageProxy: ImageProxy): Bitmap? {

        val image = imageProxy.image ?: return null

        // Initialise Buffer
        if (!::bitmapBuffer.isInitialized) {
            // The image rotation and RGB image buffer are initialized only once
            Log.d("TFLite", "Initalise toBitmap()")
            rotationMatrix = Matrix()
            rotationMatrix.postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
            bitmapBuffer = Bitmap.createBitmap(
                imageProxy.width, imageProxy.height, Bitmap.Config.ARGB_8888
            )
        }

        // Pass image to an image analyser
        yuvToRgbConverter.yuvToRgb(image, bitmapBuffer)

        // Create the Bitmap in the correct orientation
        return Bitmap.createBitmap(
            bitmapBuffer,
            0,
            0,
            bitmapBuffer.width,
            bitmapBuffer.height,
            rotationMatrix,
            false
        )
    }

    override fun analyze(image: ImageProxy) {
        val tensorImage = TensorImage.fromBitmap(toBitmap(image))
        val result = detector.detect(tensorImage)

        var best = 0f
        var objectRecognition = Recognition("", 0f)

        result.forEach {
            val temp = it.toRecognition()
            if(temp.confidence > best){
                objectRecognition = temp
                best = temp.confidence
            }
        }

        listener(objectRecognition)

        image.close()
    }
}