/**
 * Artificial Intelligence on Android with Kotlin
 *
 * Learner: Nguyen Truong Thinh
 * @since Kotlin 1.6.21 - JDK 1.8 (Java 8)
 * Contact me: nguyentruongthinhvn2020@gmail.com || +84393280504
 * */
package com.forever.bee.common.utils.conversions

import android.graphics.Bitmap
import android.graphics.ImageFormat
import android.graphics.Matrix
import android.graphics.Rect
import android.renderscript.*
import androidx.camera.core.ImageProxy

/**
 * Converting captured YUV images to bitmaps
 *
 * @param rs An instance of RenderScript framework.
 * @param script An intrinsic operation that handle this conversion.
 * @param bounds The bounding box.
 *
 * @author VASCO CORREIA VELOSO
 * */
fun ImageProxy.toBitmap(
    rs: RenderScript,
    script: ScriptIntrinsicYuvToRGB,
    bounds: Rect
): Bitmap {
    val yuvBytes = toYuvByteArray()

    val yuvType = Type.Builder(rs, Element.U8(rs))
        .setX(yuvBytes.size)
        .create()
    val input = Allocation.createTyped(
        rs, yuvType, Allocation.USAGE_SCRIPT
    )

    val bitmap = Bitmap.createBitmap(
        width, height, Bitmap.Config.ARGB_8888
    )
    val output = Allocation.createFromBitmap(rs, bitmap)

    input.copyFrom(yuvBytes)
    script.setInput(input)
    script.forEach(output)

    output.copyTo(bitmap)

    input.destroy()
    output.destroy()

    val matrix = Matrix()
    matrix.postRotate(imageInfo.rotationDegrees.toFloat())

    return Bitmap.createBitmap(
        bitmap, bounds.left, bounds.top,
        bounds.width(), bounds.height(),
        matrix, true
    )
}

/**
 * Converting captured YUV images to bitmaps
 *
 * @param rs An instance of RenderScript framework.
 * @param script An intrinsic operation that handle this conversion.
 *
 * @author VASCO CORREIA VELOSO
 * */
fun ImageProxy.toBitmap(
    rs: RenderScript,
    script: ScriptIntrinsicYuvToRGB): Bitmap {
    val yuvBytes = toYuvByteArray()

    val yuvType = Type.Builder(rs, Element.U8(rs))
        .setX(yuvBytes.size)
        .create()
    val input = Allocation.createTyped(
        rs, yuvType, Allocation.USAGE_SCRIPT
    )

    val bitmap = Bitmap.createBitmap(
        width, height, Bitmap.Config.ARGB_8888
    )
    val output = Allocation.createFromBitmap(rs, bitmap)

    input.copyFrom(yuvBytes)
    script.setInput(input)
    script.forEach(output)

    output.copyTo(bitmap)

    input.destroy()
    output.destroy()

    val matrix = Matrix()
    matrix.postRotate(imageInfo.rotationDegrees.toFloat())

    return Bitmap.createBitmap(
        bitmap, cropRect.left, cropRect.top,
        cropRect.width(), cropRect.height(),
        matrix, true
    )
}

/**
 * Because only RGB images are supported in TensorFlow Lite's ResizeOp, but not YUV_420_888.
 */
private fun ImageProxy.toYuvByteArray(): ByteArray {
    require(format == ImageFormat.YUV_420_888)
    { "Invalid image format" }

    val yBuffer = planes[0].buffer
    val uBuffer = planes[1].buffer
    val vBuffer = planes[2].buffer

    val ySize = yBuffer.remaining()
    val uSize = uBuffer.remaining()
    val vSize = vBuffer.remaining()

    val nv21 = ByteArray(ySize + uSize + vSize)

    yBuffer.get(nv21, 0, ySize)
    vBuffer.get(nv21, ySize, vSize)
    uBuffer.get(nv21, ySize + vSize, uSize)

    return nv21
}
