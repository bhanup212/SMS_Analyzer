package com.bhanu.smsanalyzer

import java.util.*


/**
 * Created by Bhanu Prakash Pasupula on 21,Jun-2020.
 * Email: pasupula1995@gmail.com
 */
data class SmsData(
    val date: Date,
    val timeInMills:Long,
    val body:String,
    val amount:Int,
    var tag:String=""
)