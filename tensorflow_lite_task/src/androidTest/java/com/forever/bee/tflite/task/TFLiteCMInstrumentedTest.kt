/**
 * Artificial Intelligence on Android with Kotlin
 *
 * @author Nguyen Truong Thinh
 * @since Kotlin 1.6.21 - JDK 1.8 (Java 8)
 * Contact me: nguyentruongthinhvn2020@gmail.com || +84393280504
 * */
package com.forever.bee.tflite.task

import android.graphics.BitmapFactory
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers

import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class TFLiteCMInstrumentedTest {
    @Test
    fun classify() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        // Context of the app under test.
        val appContext = instrumentation.targetContext

        val input = instrumentation.context.resources.assets.open("shirt.png")
        val bitmap = BitmapFactory.decodeStream(input)

        val classifier = TFLiteModelClassifier(appContext)
        val result = classifier.classify(bitmap)
        classifier.close()

        MatcherAssert.assertThat(result.size, Matchers.`is`(10))
        val topResult = result[0]
        MatcherAssert.assertThat(topResult.first, Matchers.`is`("T-shirt/top"))
        MatcherAssert.assertThat(topResult.second, Matchers.`is`(Matchers.greaterThan(0.50f)))
    }
}