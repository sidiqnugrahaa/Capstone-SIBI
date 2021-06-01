package com.sidiq.sibi.utils

import android.annotation.SuppressLint
import android.content.Context
import android.util.Log
import java.util.*
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.sidiq.sibi.ml.SibiModel
import com.sidiq.sibi.ui.maingame.Recognition
import com.sidiq.sibi.ui.maingame.Recognition.Companion.toLabel
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.model.Model


typealias RecognitionListener = (recognition: Recognition) -> Unit

class SibiClassifier(ctx: Context, private val listener: RecognitionListener) :
    ImageAnalysis.Analyzer {

    private val sibiModel: SibiModel by lazy{
        val compatList = CompatibilityList()
        val options = if(compatList.isDelegateSupportedOnThisDevice) {
            Log.d("TFLite", "This device is GPU Compatible ")
            Model.Options.Builder().setDevice(Model.Device.GPU).build()
        } else {
            Log.d("TFLite", "This device is GPU Incompatible ")
            Model.Options.Builder().setNumThreads(4).build()
        }
        SibiModel.newInstance(ctx, options)
    }

    override fun analyze(imageProxy: ImageProxy) {

        val bitmap : Bitmap = toBitmap(imageProxy)!!
        val tfImage = TensorImage.fromBitmap(bitmap)

        val output = sibiModel.process(tfImage)
            .probabilityAsCategoryList.apply {
                sortByDescending { it.score }
            }[0].toLabel(bitmap)

        listener(output)

        imageProxy.close()
    }

    /**
     * Convert Image Proxy to Bitmap
     */
    private val yuvToRgbConverter = YuvToRgbConverter(ctx)
    private lateinit var bitmapBuffer: Bitmap
    private lateinit var rotationMatrix: Matrix

    @SuppressLint("UnsafeExperimentalUsageError")
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

}

