package com.bhanu.smsanalyzer.extentions

import java.text.SimpleDateFormat
import java.util.*


/**
 * Created by Bhanu Prakash Pasupula on 21,Jun-2020.
 * Email: pasupula1995@gmail.com
 */
fun Date.formatToDateTime():String{
    return SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(this)
}
fun Long.toDate():String{
    val pattern = "dd MMM yyyy"
    val sdf = SimpleDateFormat(pattern)
    val date = Date().apply {
        time = this@toDate
    }
    return sdf.format(date)
}