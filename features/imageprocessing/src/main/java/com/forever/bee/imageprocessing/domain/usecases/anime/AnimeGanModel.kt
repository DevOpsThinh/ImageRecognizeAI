/**
 * Artificial Intelligence on Android with Kotlin
 *
 * @author Nguyen Truong Thinh
 * @since Kotlin 1.6.21 - JDK 1.8 (Java 8)
 * Contact me: nguyentruongthinhvn2020@gmail.com || +84393280504
 * */
package com.forever.bee.imageprocessing.domain.usecases.anime

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.util.Log
import org.tensorflow.lite.DataType
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.Rot90Op
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer

/**
 * Transforming the captured image
 * The Responsibilities of the [AnimeGanModel] class:
 * Manage the Tensorflow lite model; Resize, rotate, and normalize the input image;
 * Run inference on the input image to obtain an anime-styled output image;
 * De-formalize the output into the standard image format.
 *
 * */
class AnimeGanModel(context: Context): AutoCloseable {
    companion object {
        const val IMAGE_WIDTH = 256
        const val IMAGE_HEIGHT = 256
        private const val TAG = "AnimeGANv2"
    }

    private val inputDataType: DataType
    private val interpreter: Interpreter
    private val mFile: ByteBuffer

    init {
        val options = Interpreter.Options().apply {
            setNumThreads(4)
        }
        mFile = FileUtil.loadMappedFile(context, "AnimeGANv2_Hayao-64.tflite")
        interpreter = Interpreter(mFile, options)
        interpreter.resizeInput(0, intArrayOf(1, IMAGE_WIDTH, IMAGE_HEIGHT, 3))

        inputDataType = interpreter.getInputTensor(0).dataType()
    }

    fun process(bitmap: Bitmap, rotationDeg: Int): Bitmap {
        val ticks = System.currentTimeMillis()
        val outputBuffer = allocateOutputBuffer()
        interpreter.run(loadImage(bitmap, rotationDeg), outputBuffer.buffer.rewind())
        Log.i(TAG, "Processing time: ${System.currentTimeMillis() - ticks}ms")
        return postProcess(outputBuffer.floatArray)
    }

    private fun allocateOutputBuffer(): TensorBuffer = interpreter.getOutputTensor(0).let {
        TensorBuffer.createFixedSize(
            intArrayOf(1, IMAGE_WIDTH, IMAGE_HEIGHT, 3), it.dataType()
        )
    }

    /**
     * Loading the image into the model’s input tensor
     * */
    private fun loadImage(bitmap: Bitmap, rotationDeg: Int): ByteBuffer {
        val tensorImage = TensorImage(inputDataType)
        tensorImage.load(bitmap)
        return buildProcessor(rotationDeg)
            .process(tensorImage)
            .buffer
    }

    /**
     * The image processor is responsible for resizing, rotating, and normalizing the input image
     * */
    private fun buildProcessor(rotateDeg: Int) = ImageProcessor.Builder()
        .add(
            ResizeOp(IMAGE_WIDTH, IMAGE_HEIGHT, ResizeOp.ResizeMethod.BILINEAR)
        )
        .add(
            Rot90Op(4 - rotateDeg / 90)
        )
        .add(
            NormalizeOp(127.5f, 127.5f)
        )
        .build()

    private fun postProcess(data: FloatArray): Bitmap {
        val pixelCount = (data.size / 3)
        val pixels = IntArray(pixelCount)
        var floatPos = 0
        for (i in 0 until pixelCount) {
            pixels[i] = Color.rgb(
                ((data[floatPos++] + 1.0f) / 2.0f * 255.0f).toInt(),
                ((data[floatPos++] + 1.0f) / 2.0f * 255.0f).toInt(),
                ((data[floatPos++] + 1.0f) / 2.0f * 255.0f).toInt()
            )
        }
        return  Bitmap.createBitmap(pixels, IMAGE_WIDTH, IMAGE_HEIGHT, Bitmap.Config.ARGB_8888)
    }

    override fun close() {
        interpreter.close()
    }
}