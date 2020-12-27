package com.sildian.apps.togetrail.common.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Paint.ANTI_ALIAS_FLAG
import android.graphics.drawable.Drawable
import android.graphics.drawable.VectorDrawable
import android.os.Build
import androidx.appcompat.content.res.AppCompatResources
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.sildian.apps.togetrail.R

/*************************************************************************************************
 * Provides with some functions allowing to create markers on a map
 ************************************************************************************************/

object MapMarkersUtilities {

    /**
     * Creates a map marker from a vector
     * @param context : the context
     * @param resId : the resource id
     * @param text : an optional text to be drawn on the marker
     * @return a BitmapDescriptor
     */

    @JvmStatic
    fun createMapMarkerFromVector(context: Context?, resId: Int, text:String?=null): BitmapDescriptor? {
        return if (context != null) {
            val drawable = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                AppCompatResources.getDrawable(context, resId) as VectorDrawable
            } else {
                AppCompatResources.getDrawable(context, resId) as Drawable
            }
            val w = drawable.intrinsicWidth
            val h = drawable.intrinsicHeight
            drawable.setBounds(0, 0, w, h)
            val bm = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bm)
            drawable.draw(canvas)
            if (text != null) drawTextOnMarker(context, canvas, text)
            BitmapDescriptorFactory.fromBitmap(bm)
        } else null
    }

    /**
     * Draws a text on a map marker
     * @param context : the context
     * @param canvas : the canvas allowing to draw
     * @param text : the text to be drawn on the marker
     */

    @JvmStatic
    private fun drawTextOnMarker(context:Context, canvas:Canvas, text:String){
        val textPaint = Paint(ANTI_ALIAS_FLAG).apply {
            color = Color.BLACK
            textSize = context.resources.getDimension(R.dimen.text_size_small)
            style = Paint.Style.FILL_AND_STROKE
            strokeWidth=2f
        }
        val textWidth=textPaint.measureText(text)
        val x=(canvas.width/2).toFloat()-(textWidth/2)
        val y=(canvas.height/2).toFloat()
        canvas.drawText(text, x, y, textPaint)
    }
}