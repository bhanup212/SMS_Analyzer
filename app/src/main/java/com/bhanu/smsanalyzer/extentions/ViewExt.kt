package com.bhanu.smsanalyzer.extentions

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.widget.LinearLayout
import com.bhanu.smsanalyzer.R


/**
 * Created by Bhanu Prakash Pasupula on 21,Jun-2020.
 * Email: pasupula1995@gmail.com
 */

fun View.toBitmap(): Bitmap {
    val bitmap = Bitmap.createBitmap(this.width,this.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bitmap)
    this.draw(canvas)
    return bitmap
}