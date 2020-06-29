package com.bhanu.smsanalyzer.extentions

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.content.FileProvider
import java.io.File


/**
 * Created by Bhanu Prakash Pasupula on 21,Jun-2020.
 * Email: pasupula1995@gmail.com
 */
@RequiresApi(Build.VERSION_CODES.M)
fun Context.isPermissionGranted(permission:String):Boolean{
    return checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED
}

fun Context.shareFile(file: File){
    val contentUri: Uri = FileProvider.getUriForFile(this, "com.bhanu.smsanalyzer", file);

    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION) // temp permission for receiving app to read this file
        setDataAndType(contentUri, this@shareFile.contentResolver.getType(contentUri))
        putExtra(Intent.EXTRA_STREAM, contentUri)
    }

    this.startActivity(Intent.createChooser(shareIntent, "Share using"))
}