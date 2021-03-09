package com.sildian.apps.togetrail.common.dataBinding

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions

/*************************************************************************************************
 * Defines specific data binding adapters
 ************************************************************************************************/

object DataBindingAdapters {

    @JvmStatic
    @BindingAdapter("app:srcCompat")
    fun bindSrcCompat(imageView: ImageView, drawable: Drawable) {
        imageView.setImageDrawable(drawable)
    }

    @JvmStatic
    @BindingAdapter("imgUrl", "imgPlaceHolder", "imgOptions")
    fun bindLoadImage(imageView: ImageView, imgUrl: String?, imgPlaceHolder: Drawable, imgOptions: RequestOptions) {
        if (imgUrl != null) {
            Glide.with(imageView)
                .load(imgUrl)
                .apply(imgOptions)
                .placeholder(imgPlaceHolder)
                .into(imageView)
        }
        else {
            imageView.setImageDrawable(imgPlaceHolder)
        }
    }
}