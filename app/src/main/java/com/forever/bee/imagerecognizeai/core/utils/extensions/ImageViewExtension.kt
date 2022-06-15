package com.forever.bee.imagerecognizeai.core.utils.extensions

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

fun ImageView.setImage(url: String) {
    Glide.with(this.context)
        .load(url.ifEmpty { null })
        //Disable disk cache here: for Glide image loading library
        .diskCacheStrategy(DiskCacheStrategy.NONE)
        .into(this)
}