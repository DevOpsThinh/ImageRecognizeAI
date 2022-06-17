/**
 * Artificial Intelligence on Android with Kotlin
 *
 * Learner: Nguyen Truong Thinh
 * @since Kotlin 1.6.21 - JDK 1.8 (Java 8)
 * Contact me: nguyentruongthinhvn2020@gmail.com || +84393280504
 * */
package com.forever.bee.common

import android.graphics.Bitmap
import java.io.Closeable

/**
 * A module for TensorFlow Lite APIs
 * The [Classifier] interface that extended from the [Closeable]
 *
 * @author VASCO CORREIA VELOSO
 * */
interface Classifier : Closeable {
    /**
     * The classification processing
     *
     * @param bitmap An instance of the [Bitmap]
     * */
    fun classify(bitmap: Bitmap): List<Pair<String, Float>>
}