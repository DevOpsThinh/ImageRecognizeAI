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
import androidx.camera.core.ImageProxy
import com.forever.bee.common.utils.conversions.toBitmapFromJpeg
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Convert a Jpeg-encoded image into an Android bitmap format & to run the image processing task
 * as a coroutine.
 * */
class AnimeTransformation(context: Context): AutoCloseable {
    private val animeModel = AnimeGanModel(context)

    /**
     * Handle converting a Jpeg-encoded image into an Android bitmap format
     * */
    suspend fun transform(proxy: ImageProxy): Bitmap = withContext(Dispatchers.IO) {
        val bitmap = toBitmapFromJpeg(proxy)
        return@withContext animeModel.process(
            bitmap, proxy.imageInfo.rotationDegrees
        )
    }

    override fun close() {
        animeModel.close()
    }
}