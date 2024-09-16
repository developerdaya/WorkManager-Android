package com.developerdaya.gpstrackingapp.services.manager

import android.app.KeyguardManager
import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager
import android.content.Context

class UsageTrackingHelper(private val context: Context) {

    fun getUsageStats(): List<UsageStats>? {
        val usageStatsManager = context.getSystemService(Context.USAGE_STATS_SERVICE) as UsageStatsManager
        val currentTime = System.currentTimeMillis()
        val usageEvents = usageStatsManager.queryUsageStats(
            UsageStatsManager.INTERVAL_DAILY, currentTime - 1000 * 3600 * 24, currentTime
        )
        return usageEvents
    }

    fun isScreenUnlocked(): Boolean {
        val keyguardManager = context.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
        return !keyguardManager.isKeyguardLocked
    }
}
