package com.developerdaya.gpstrackingapp.services

import android.Manifest
import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import androidx.room.Room
import com.developerdaya.gpstrackingapp.MainActivity
import com.developerdaya.gpstrackingapp.R
import com.developerdaya.gpstrackingapp.room.AppDatabase
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices

class ForegroundService : Service() {
    private lateinit var db: AppDatabase

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onCreate() {
        super.onCreate()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        db = Room.databaseBuilder(applicationContext, AppDatabase::class.java, "app_db").build()

        // Start location updates
        startLocationUpdates()

        // Start tracking app usage and screen unlocks
        trackAppUsage()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // Create a notification for the foreground service
        val notification = createNotification()
        startForeground(1, notification)

        return START_STICKY
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent,
            PendingIntent.FLAG_IMMUTABLE)

        return NotificationCompat.Builder(this, "tracking_channel")
            .setContentTitle("Tracking Service")
            .setContentText("Tracking your location and usage")
            .setSmallIcon(R.drawable.baseline_gps_fixed_24)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun startLocationUpdates() {
        val locationRequest = LocationRequest.create().apply {
            interval = 10000 // 10 seconds
            fastestInterval = 5000
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
            == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    val location: Location? = locationResult.lastLocation
                    location?.let {
                        // Log or save location
                        Log.d("Location", "Location: ${it.latitude}, ${it.longitude}")
                        // Save location to Room database
                    }
                }
            }, Looper.getMainLooper())
        }
    }

    private fun trackAppUsage() {
        // Implement tracking of app usage and screen unlocks
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
