package com.bhanu.smsanalyzer.manager

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.bhanu.smsanalyzer.SmsData
import com.bhanu.smsanalyzer.extentions.formatToDateTime
import com.bhanu.smsanalyzer.extentions.isPermissionGranted
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList
import kotlin.collections.HashMap


/**
 * Created by Bhanu Prakash Pasupula on 21,Jun-2020.
 * Email: pasupula1995@gmail.com
 */
class SmsWorkManager(val context: Context, workerParam: WorkerParameters) :
    CoroutineWorker(context, workerParam) {

    companion object {
        private val TAG = "SmsWorkManager"
        const val Expenses = "Expenses"
        const val Income = "Income"

        internal val creditHashMap = HashMap<Date,ArrayList<SmsData>>()
        internal val debitHashMap = HashMap<Date,ArrayList<SmsData>>()

        /**
         * This doublePattern matches 200.00 or 4,000.00 pattern
         */
        private const val doublePattern =
            "([0-9][0-9]?(,?[0-9]))?([0-9](,[0-9]))?(\\d+\\.\\d+)"  // "\\d+\\.\\d+"

        /**
         * This inrPattern matches INR 120 or INR2000.00
         * and rsPattern matches Rs 400 or Rs300
         */
        private const val inrPattern = "((?:INR|Rs)[^a-zA-Z]([0-9]+))"
    }

    private val debitList = ArrayList<Int>()
    private val creditList = ArrayList<Int>()

    @SuppressLint("MissingPermission")
    override suspend fun doWork(): Result {
        Log.e(TAG, "sms: doWork")
        return try {
            readSms()
            val expenses = debitList.sum()
            val income = creditList.sum()
            val output = workDataOf(Expenses to expenses, Income to income)
            Result.success(output)
        } catch (e: Exception) {
            Result.failure()
        }
    }

    private fun readSms() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !context.isPermissionGranted(android.Manifest.permission.READ_SMS)) {
            Log.e(TAG, "SMS permission not granted")
            return
        }
        val cr = context.contentResolver
        val cursor = cr.query(Uri.parse("content://sms/"), null, null, null, null)

        creditHashMap.clear()
        debitHashMap.clear()
        if (cursor != null) {
            while (cursor.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndex("_id"))
                val address = cursor.getString(cursor.getColumnIndex("address"))
                val body = cursor.getString(cursor.getColumnIndex("body"))
                val timeStampMills = cursor.getLong(cursor.getColumnIndex("date"))

                try {
                    if (body.isNotEmpty() && body.contains("debited")) {
                        parseAmount(true, body,timeStampMills)
                    } else if (body.isNotEmpty() && body.contains("credited")) {
                        parseAmount(false, body,timeStampMills)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "parsing error: ${e.message}")
                }

            }
            cursor.close()
        }
    }

    private fun parseAmount(isDebit: Boolean, body: String,timeInMills:Long) {
        var amount = 0;
        val inrMatcher = Pattern.compile(inrPattern).matcher(body)
        if (inrMatcher.find()) {
            val amountStr: String =
                inrMatcher.group().replace("INR", "").replace("Rs", "").replace(".", "")
            amount = amountStr.replace("\\s".toRegex(), "").toInt()

        } else {
            val matcher = Pattern.compile(doublePattern).matcher(body)
            if (matcher.find()) {
                amount = matcher.group().toDouble().toInt()
            }
        }

        val date = Date(timeInMills)
        if (isDebit) {
            debitList.add(amount)
            if (debitHashMap[date].isNullOrEmpty()){
                debitHashMap[date] = ArrayList<SmsData>().apply { add(SmsData(date,timeInMills,body,amount)) }
            }else{
                debitHashMap[date]?.apply {
                    add(SmsData(date,timeInMills,body,amount))
                }
            }
        } else {
            creditList.add(amount)
            if (creditHashMap[date].isNullOrEmpty()){
                creditHashMap[date] = ArrayList<SmsData>().apply { add(SmsData(date,timeInMills,body,amount)) }
            }else{
                creditHashMap[date]?.apply {
                    add(SmsData(date,timeInMills,body,amount))
                }
            }
        }

    }

}