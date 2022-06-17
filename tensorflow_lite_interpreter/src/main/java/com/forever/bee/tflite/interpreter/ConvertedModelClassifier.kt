/**
 * Artificial Intelligence on Android with Kotlin
 *
 * Learner: Nguyen Truong Thinh
 * @since Kotlin 1.6.21 - JDK 1.8 (Java 8)
 * Contact me: nguyentruongthinhvn2020@gmail.com || +84393280504
 * */
package com.forever.bee.tflite.interpreter

import android.content.Context
import android.graphics.Bitmap
import com.forever.bee.common.Classifier
import org.tensorflow.lite.Interpreter
import org.tensorflow.lite.Tensor
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.gpu.GpuDelegate
import org.tensorflow.lite.support.common.FileUtil
import org.tensorflow.lite.support.common.TensorProcessor
import org.tensorflow.lite.support.common.ops.NormalizeOp
import org.tensorflow.lite.support.image.ImageProcessor
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.image.ops.ResizeOp
import org.tensorflow.lite.support.image.ops.TransformToGrayscaleOp
import org.tensorflow.lite.support.label.TensorLabel
import org.tensorflow.lite.support.tensorbuffer.TensorBuffer
import java.nio.ByteBuffer

/**
 * A module for TensorFlow Lite APIs
 * Working with a converted TensorFlow Lite image classification model
 *
 * @param context An Android [Context] reference is necessary to init the [labels] classification label,
 * the [modelFile] TensorFlow Lite model.
 * @property interpreter An instance of the [Interpreter] class
 * @property modelFile A converted TensorFlow Lite image classification model
 * @property inputTensor An instance of [Tensor]
 * @property outputBuffer An instance of [TensorBuffer]
 * @property outputProcessor The output tensor processing pipelines from [TensorProcessor] class
 * @property labels The classification labels
 *
 * @author VASCO CORREIA VELOSO
 * */
class ConvertedModelClassifier(context: Context) : Classifier {
    private val interpreter: Interpreter
    private val modelFile: ByteBuffer

    private val inputTensor: Tensor
    private val outputBuffer: TensorBuffer
    private val outputProcessor: TensorProcessor = TensorProcessor.Builder()
        .add(NormalizeOp(0.0f, 1.0f))
        .build()

    private val labels: List<String> = FileUtil.loadLabels(
        context, "labels-fashion.txt"
    )

    init {
        val compatibility = CompatibilityList()
        val options = Interpreter.Options().apply {
            if (compatibility.isDelegateSupportedOnThisDevice) {
                addDelegate(
                    GpuDelegate(
                        compatibility.bestOptionsForThisDevice
                    )
                )
            }
        }
        compatibility.close()

        modelFile = FileUtil.loadMappedFile(
            context, "converted-fashion.tflite"
        )
        interpreter = Interpreter(modelFile, options)

        inputTensor = interpreter.getInputTensor(0)
        val outputTensor = interpreter.getOutputTensor(0)
        outputBuffer = TensorBuffer.createFixedSize(
            outputTensor.shape(), outputTensor.dataType()
        )
    }

    /**
     * Combining the image loading & the classification functions into one single operation.
     * */
    override fun classify(bitmap: Bitmap): List<Pair<String, Float>> {
        return classify(loadImage(bitmap))
    }

    override fun close() {
        interpreter.close()
    }

    /**
     * The classification functions
     *
     * @return A list of all classes and their confidence levels as a probability,
     * sorted in descending order for update the UI with the most recent classifications.
     * */
    private fun classify(image: TensorImage): List<Pair<String, Float>> {
        interpreter.run(
            image.buffer,
            outputBuffer.buffer.rewind()
        )

        return TensorLabel(labels, outputProcessor.process(outputBuffer))
            .mapWithFloatValue
            .map { (key, value) -> Pair(key, value * 100.0f) }
            .sortedByDescending { (_, value) -> value }
    }

    /**
     * The image that is to be classified is loaded and transformed under the form of a [TensorImage] object
     * */
    private fun loadImage(bitmap: Bitmap): TensorImage {
        val inputShape = inputTensor.shape() // {1, width, height, channels}
        val imageHeight = inputShape[1]
        val imageWidth = inputShape[2]
        //  a third-order tensor: Color Images
        val tensorImage = TensorImage(inputTensor.dataType())
        tensorImage.load(bitmap)

        return ImageProcessor.Builder()
            .add(ResizeOp(imageWidth, imageHeight, ResizeOp.ResizeMethod.BILINEAR))
            .add(TransformToGrayscaleOp())
            .build()
            .process(tensorImage)
    }
}