package com.developerdaya.gpstrackingapp.gps


import android.annotation.SuppressLint
import android.app.Service
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log

class GPSTracker(private val mContext: Context) : Service(), LocationListener {

    var isGPSEnabled = false
    var isNetworkEnabled = false
    var canGetLocation = false
    var location: Location? = null
    var latitude: Double = 0.0
    var longitude: Double = 0.0

    private val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 10f // 10 meters
    private val MIN_TIME_BW_UPDATES: Long = 1000 * 60 // 1 minute

    private var locationManager: LocationManager? = null

    init {
        fetchLocation()
    }

    @SuppressLint("MissingPermission")
    fun fetchLocation(): Location? {
        try {
            locationManager = mContext.getSystemService(LOCATION_SERVICE) as LocationManager

            isGPSEnabled = locationManager!!.isProviderEnabled(LocationManager.GPS_PROVIDER)
            isNetworkEnabled = locationManager!!.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

            if (!isGPSEnabled && !isNetworkEnabled) {
                Log.d("no network provider", "no network provider is enabled")
            } else {
                this.canGetLocation = true

                if (isNetworkEnabled) {
                    locationManager!!.requestLocationUpdates(
                        LocationManager.NETWORK_PROVIDER,
                        MIN_TIME_BW_UPDATES,
                        MIN_DISTANCE_CHANGE_FOR_UPDATES, this
                    )
                    Log.d("Network", "Network")
                    if (locationManager != null) {
                        location = locationManager!!.getLastKnownLocation(LocationManager.NETWORK_PROVIDER)
                        if (location != null) {
                            latitude = location!!.latitude
                            longitude = location!!.longitude
                        }
                    }
                }

                if (isGPSEnabled) {
                    if (location == null) {
                        locationManager!!.requestLocationUpdates(
                            LocationManager.GPS_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this
                        )
                        Log.d("GPS Enabled", "GPS Enabled")
                        if (locationManager != null) {
                            location = locationManager!!.getLastKnownLocation(LocationManager.GPS_PROVIDER)
                            if (location != null) {
                                latitude = location!!.latitude
                                longitude = location!!.longitude
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return location
    }

    override fun onLocationChanged(location: Location) {
        // Handle location changes here
    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {
        // Handle status changes here
    }

    override fun onProviderEnabled(provider: String) {
        // Handle provider enabled here
    }

    override fun onProviderDisabled(provider: String) {
        // Handle provider disabled here
    }

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
