/**
 * Artificial Intelligence on Android with Kotlin
 *
 * Learner: Nguyen Truong Thinh
 * @since Kotlin 1.6.21 - JDK 1.8 (Java 8)
 * Contact me: nguyentruongthinhvn2020@gmail.com || +84393280504
 * */
package com.forever.bee.imagerecognization

import android.content.Context
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicYuvToRGB
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.forever.bee.common.Classifier
import com.forever.bee.common.utils.conversions.toBitmap
import java.io.Closeable
import java.util.concurrent.locks.Lock
import java.util.concurrent.locks.ReentrantLock

/**
 * The [ImageAnalyser] class, which extended with the YUV images to bitmaps conversion
 *
 * @param context An Android [Context] reference is necessary to init the [RenderScript] engine.
 * @param classifier An instance of [Classifier] class
 *
 * @author VASCO CORREIA VELOSO
 * */
class ImageAnalyser(
    context: Context,
    private val classifier: Classifier,
    private val resultListener: (List<Pair<String, Float>>) -> Unit
) : ImageAnalysis.Analyzer, Closeable {
    private val rs = RenderScript.create(context)
    private val script = ScriptIntrinsicYuvToRGB.create(rs, Element.U8_4(rs))
    private val closureLock: Lock = ReentrantLock()
    private var closed: Boolean = false

    override fun analyze(imgProxy: ImageProxy) {
        try {
            closureLock.lock()

            if (!closed) {
                val result = classifier.classify(
                    imgProxy.toBitmap(rs, script)
                )
                resultListener(result)
            }
        } finally {
            closureLock.unlock()
            imgProxy.close()
        }
    }

    override fun close() {
        try {
            closureLock.lock()
            closed = true
            classifier.close()
        } finally {
            closureLock.unlock()
        }
    }
}