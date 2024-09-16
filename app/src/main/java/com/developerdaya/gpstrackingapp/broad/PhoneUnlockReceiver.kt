package com.developerdaya.gpstrackingapp.broad

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.developerdaya.gpstrackingapp.util.SharedPreferenceUtil
import com.developerdaya.gpstrackingapp.workManager.LocationWorker
import java.util.concurrent.TimeUnit

class PhoneUnlockReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        if (intent?.action == Intent.ACTION_USER_PRESENT) {
            val currentTime = System.currentTimeMillis()
            SharedPreferenceUtil.getInstance(context).mLastUnlockedTime = currentTime
            Log.d("PhoneUnlockReceiver", "onReceive: Phone Unlocked ")
        }
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED) {
            val workRequest = PeriodicWorkRequestBuilder<LocationWorker>(15, TimeUnit.MINUTES)
                .build()
            context?.let {
                WorkManager.getInstance(it).enqueue(workRequest)
            }
        }


    }


}
