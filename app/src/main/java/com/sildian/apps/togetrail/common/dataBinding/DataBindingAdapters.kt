package com.sildian.apps.togetrail.common.dataBinding

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter

/*************************************************************************************************
 * Defines specific data binding adapters
 ************************************************************************************************/

object DataBindingAdapters {

    @JvmStatic
    @BindingAdapter("app:srcCompat")
    fun bindSrcCompat(imageView: ImageView, drawable: Drawable) {
        imageView.setImageDrawable(drawable)
    }
}