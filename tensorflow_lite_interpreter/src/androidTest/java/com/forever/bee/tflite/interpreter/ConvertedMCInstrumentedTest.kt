package com.forever.bee.tflite.interpreter

import android.graphics.BitmapFactory
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.hamcrest.Matchers.`is`
import org.hamcrest.Matchers.greaterThan
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test
import org.junit.runner.RunWith



/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ConvertedMCInstrumentedTest {
    @Test
    fun classify() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        // Context of the app under test.
        val appContext = instrumentation.targetContext

        val input = instrumentation.context.resources.assets.open("shirt.png")
        val bitmap = BitmapFactory.decodeStream(input)

        val classifier = ConvertedModelClassifier(appContext)
        val result = classifier.classify(bitmap)
        classifier.close()

        assertThat(result.size, `is`(10))
        val topResult = result[0]
        assertThat(topResult.first, `is`("T-shirt/top"))
        assertThat(topResult.second, `is`(greaterThan(0.90f)))
    }
}