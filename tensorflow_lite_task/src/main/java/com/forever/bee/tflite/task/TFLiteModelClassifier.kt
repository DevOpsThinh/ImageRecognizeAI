/**
 * Artificial Intelligence on Android with Kotlin
 *
 * Learner: Nguyen Truong Thinh
 * @since Kotlin 1.6.21 - JDK 1.8 (Java 8)
 * Contact me: nguyentruongthinhvn2020@gmail.com || +84393280504
 * */
package com.forever.bee.tflite.task

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import com.forever.bee.common.Classifier
import com.forever.bee.tflite.task.wrapper.FashionMnistModel
import org.tensorflow.lite.TensorFlowLite.init
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.model.Model

/**
 * A module for TensorFlow Lite APIs
 * Working with a trained existing TensorFlow Lite image classification model
 *
 * @param context An Android [Context] reference is necessary to init the [FashionMnistModel]
 * TensorFlow Lite model.
 * @property model An instance of [FashionMnistModel] TensorFlow Lite model.
 *
 * @author VASCO CORREIA VELOSO
 * */
class TFLiteModelClassifier(context: Context) : Classifier {
    private val model: FashionMnistModel

    init {
        val options = Model.Options.Builder().apply {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                setDevice(Model.Device.NNAPI)
            }
        }.build()

        model = FashionMnistModel.newInstance(context, options)
    }

    override fun classify(bitmap: Bitmap): List<Pair<String, Float>> {
        return model.process(TensorImage.fromBitmap(bitmap))
            .probabilityAsCategoryList
            .sortedByDescending { category -> category.score }
            .map { category -> Pair(category.label, category.score * 100.0f) }
    }

    override fun close() {
        // Releases model resources if no longer used.
        model.close()
    }
}