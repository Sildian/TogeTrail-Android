package com.sildian.apps.togetrail.common.dataBinding

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.view.View
import android.widget.CompoundButton
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.sildian.apps.circularsliderlibrary.CircularSlider
import com.sildian.apps.circularsliderlibrary.ValueFormatter

/*************************************************************************************************
 * Defines specific data binding adapters
 ************************************************************************************************/

object DataBindingAdapters {

    @JvmStatic
    @BindingAdapter("app:srcCompat")
    fun bindSrcCompat(imageView: ImageView, drawable: Drawable?) {
        imageView.setImageDrawable(drawable)
    }

    @JvmStatic
    @BindingAdapter("typeFace")
    fun bindTypeFace(textView: TextView, typeFace: Typeface?) {
        textView.typeface = typeFace
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

    @JvmStatic
    @BindingAdapter("onCheckedChanged")
    fun bindOnCheckedChanged(compoundButton: CompoundButton, listener: CompoundButton.OnCheckedChangeListener?) {
        compoundButton.setOnCheckedChangeListener(listener)
    }

    @JvmStatic
    @BindingAdapter("valueFormatter")
    fun bindValueFormatter(circularSlider: CircularSlider, valueFormatter: ValueFormatter?) {
        circularSlider.valueFormatter = valueFormatter
    }

    @JvmStatic
    @BindingAdapter("onValueChanged")
    fun bindOnValueChanged(circularSlider: CircularSlider, listener: CircularSlider.OnValueChangedListener?) {
        listener?.let {
            circularSlider.addOnValueChangedListener(listener)
        }
    }
}