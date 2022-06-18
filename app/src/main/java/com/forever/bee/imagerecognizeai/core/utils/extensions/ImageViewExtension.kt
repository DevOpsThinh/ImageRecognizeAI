/**
 * Artificial Intelligence on Android with Kotlin
 *
 * @author Nguyen Truong Thinh
 * @since Kotlin 1.6.21 - JDK 1.8 (Java 8)
 * Contact me: nguyentruongthinhvn2020@gmail.com || +84393280504
 * */
package com.forever.bee.imagerecognizeai.core.utils.extensions

import android.annotation.SuppressLint
import android.content.Context
import android.os.Environment
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
@Throws(IOException::class)
fun createUniqueImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMddHHmmss").format(Date())
    val filename = "image_" + timeStamp + "_"
    val filesDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)

    return File.createTempFile(filename, ".jpg", filesDir)
}

fun ImageView.setImage(url: String) {
    Glide.with(this.context)
        .load(url.ifEmpty { null })
        //Disable disk cache here: for Glide image loading library
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .into(this)
}