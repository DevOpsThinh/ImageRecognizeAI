/**
 * Artificial Intelligence on Android with Kotlin
 *
 * @author Nguyen Truong Thinh
 * @since Kotlin 1.6.21 - JDK 1.8 (Java 8)
 * Contact me: nguyentruongthinhvn2020@gmail.com || +84393280504
 * */
package com.forever.bee.facedetection

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicYuvToRGB
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.forever.bee.common.utils.conversions.toBitmap
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions
import com.google.mlkit.vision.face.FaceDetectorOptions.PERFORMANCE_MODE_FAST

/**
 * The [RealtimeFaceDetector] image analysis class for scanning images for faces in real-time
 *
 * @author VASCO CORREIA VELOSO
 * */
class RealtimeFaceDetector(context: Context, private val receiver: (Bitmap?) -> Unit) :
    ImageAnalysis.Analyzer {

    private val options: FaceDetectorOptions = FaceDetectorOptions.Builder()
        .setPerformanceMode(PERFORMANCE_MODE_FAST)
        .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
        .setContourMode(FaceDetectorOptions.CONTOUR_MODE_NONE)
        .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
        .setMinFaceSize(0.1f)
        .build()

    private val detector: FaceDetector = FaceDetection.getClient(options)

    private val rsFramework = RenderScript.create(context)
    private val script = ScriptIntrinsicYuvToRGB.create(rsFramework, Element.U8_4(rsFramework))

    override fun analyze(imageProxy: ImageProxy) {
        val originalIMG = imageProxy.image

        if (originalIMG == null) {
            imageProxy.close()
            return
        }
        detector.process(
            InputImage.fromMediaImage(
                originalIMG,
                imageProxy.imageInfo.rotationDegrees
            )
        )
            .addOnSuccessListener { faces ->
                val faceBitmap = faces.firstOrNull()?.let { face ->
                    val faceBounds = cancelRotation(face.boundingBox, imageProxy)
                    if (faceBounds.setIntersect(imageProxy.cropRect, faceBounds)) {
                        imageProxy.toBitmap(rsFramework, script, faceBounds)
                    } else {
                        null
                    }
                }
                receiver(faceBitmap)
            }
            .addOnFailureListener { receiver(null) }
            .addOnCompleteListener { imageProxy.close() }
    }

    /**
     * Because the conversion from the ImageProxy to a Bitmap crops first and rotates
     * second, we need to cancel any rotation already applied to the face bounding rectangle.
     * */
    private fun cancelRotation(bounds: Rect, imgProxy: ImageProxy): Rect {
        val translatedRect = RectF(bounds)
        val matrix = Matrix()
        matrix.setRotate(
            -imgProxy.imageInfo.rotationDegrees.toFloat(),
            imgProxy.cropRect.centerX().toFloat(), imgProxy.cropRect.centerY().toFloat()
        )
        matrix.mapRect(translatedRect)

        return Rect(
            translatedRect.left.toInt(),
            translatedRect.top.toInt(),
            translatedRect.right.toInt(),
            translatedRect.bottom.toInt()
        )
    }
}