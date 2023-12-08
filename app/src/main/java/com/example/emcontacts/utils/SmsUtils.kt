package com.example.emcontacts.utils

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log

object SmsUtils {
    fun sendSMS(context: Context, phoneNumber: String, message: String) {
        Log.d("SmsUtils", "Sending SMS to $phoneNumber with message: $message")
        val smsIntent = Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:$phoneNumber"))
        smsIntent.putExtra("sms_body", message)
        try {
            context.startActivity(smsIntent)
        } catch (e: Exception) {
            Log.e("SmsUtils", "Error sending SMS", e)
        }
    }
}