package com.developerdaya.gpstrackingapp

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.developerdaya.gpstrackingapp.adapter.LocationAdapter
import com.developerdaya.gpstrackingapp.broad.PhoneUnlockReceiver
import com.developerdaya.gpstrackingapp.databinding.ActivityMainBinding
import com.developerdaya.gpstrackingapp.databinding.GpsDialogBinding
import com.developerdaya.gpstrackingapp.gps.GPSTracker
import com.developerdaya.gpstrackingapp.model.LocationData
import com.developerdaya.gpstrackingapp.room.LocationEntity
import com.developerdaya.gpstrackingapp.services.ForegroundService
import com.developerdaya.gpstrackingapp.util.SharedPreferenceUtil
import com.developerdaya.gpstrackingapp.viewModel.LocationViewModel
import com.developerdaya.gpstrackingapp.workManager.LocationWorker
import com.google.android.material.bottomsheet.BottomSheetDialog
import java.text.SimpleDateFormat
import java.util.Date
import java.util.concurrent.TimeUnit
//---------------------------DEVELOPER DAYA------------------------------//
class MainActivity : AppCompatActivity() {
    private val PERMISSION_REQUEST_CODE = 100
    private lateinit var binding: ActivityMainBinding
    private lateinit var locationViewModel: LocationViewModel
    private lateinit var locationAdapter: LocationAdapter
    private val REQUEST_LOCATION_PERMISSION = 101
    var liveLocation: Location? = null
    private val handler = Handler(Looper.getMainLooper())
    private val runnable = object : Runnable { override fun run() {
            getCurrentLocationAndSave()
            handler.postDelayed(this, 5000)
            if (GPSTracker(this@MainActivity).isGPSEnabled) {
                liveLocation = GPSTracker(this@MainActivity).location
                liveLocation?.let {
                    val timestamp = System.currentTimeMillis()
                    val geocoder = Geocoder(this@MainActivity)
                    val addresses = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                    var buildingNo = addresses?.get(0)?.featureName
                    var sector = addresses?.get(0)?.subLocality
                    var city = addresses?.get(0)?.locality
                    val state = addresses?.get(0)?.adminArea
                    val postalCode = addresses?.get(0)?.postalCode
                    val country = addresses?.get(0)?.countryName
                    val address = "$sector $city, $state"
                    if (binding.mSwitchCompat.isChecked) {
                        Log.d(
                            "LocationUpdate",
                            "Latitude: ${it.latitude}, Longitude: ${it.longitude}, Timestamp: ${
                                mTimeStampToDate(timestamp)
                            } $address"
                        )
                        var time2 = " ${mTimeStampToDate(timestamp)}"

                        val locationEntity = LocationEntity(
                            0,
                            latitude = it.latitude,
                            longitude = it.longitude,
                            timestamp = time2,
                            address = address
                        )

                        SharedPreferenceUtil.getInstance(this@MainActivity).mLastKnownLocation =
                            address
                        locationViewModel.insertLocation(locationEntity)
                    }


                }


            }
        } }
    fun mTimeStampToDate(value: Long): String {
        var timestamp = value
        if (timestamp.toString().length < 13) {
            timestamp *= 1000
        }
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa")
        return sdf.format(Date(timestamp))
    }
    private fun getCurrentLocationAndSave() {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val locationListener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                val latitude = location.latitude
                val longitude = location.longitude
                val timestamp = System.currentTimeMillis().toString()
                Log.d(
                    "LocationUpdate",
                    "Latitude: $latitude, Longitude: $longitude, Timestamp:}"
                )

                // Save the location to the database
                val locationEntity = LocationEntity(
                    0,
                    latitude = latitude,
                    longitude = longitude,
                    timestamp = timestamp, ""
                )
                locationViewModel.insertLocation(locationEntity)
            }

            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {}
            override fun onProviderEnabled(provider: String) {}
            override fun onProviderDisabled(provider: String) {}
        }

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                1000,
                0f,
                locationListener
            ) // Update every second
        }
    }
    private var isReceiverRegistered = false
    private lateinit var phoneUnlockReceiver: PhoneUnlockReceiver
    override fun onResume() {
        super.onResume()
        if (!isReceiverRegistered) {
            phoneUnlockReceiver = PhoneUnlockReceiver() // Initialize the receiver
            val filter = IntentFilter(Intent.ACTION_USER_PRESENT)
            registerReceiver(phoneUnlockReceiver, filter)
            isReceiverRegistered = true
        }
        binding.lastUnlockedTime2.text =
            mTimeStampToDate(SharedPreferenceUtil.getInstance(this).mLastUnlockedTime)

    }
    override fun onPause() {
        super.onPause()
        if (isReceiverRegistered) {
            unregisterReceiver(phoneUnlockReceiver)
            isReceiverRegistered = false
        }
    }
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(runnable) // Stop periodic task
    }
    fun openGPSSheet(context: Context) {
        val dialog = BottomSheetDialog(context)
        val binding = GpsDialogBinding.inflate(LayoutInflater.from(context))
        dialog.setContentView(binding.root)
        dialog.show()
        dialog.setCancelable(false)
        binding.mSetting.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))

        }
        binding.mCancel.setOnClickListener { dialog.dismiss() }
    }
    private fun scheduleLocationWork() {
        val workRequest = PeriodicWorkRequestBuilder<LocationWorker>(15, TimeUnit.MINUTES).build()
        WorkManager.getInstance(this).enqueue(workRequest)
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        initControl()
        observer()
    }
    fun observer() {
        locationViewModel.getAllLocations().observe(this, Observer { locationEntities ->
            val locationDataList = locationEntities.map { entity ->
                LocationData(
                    latitude = entity.latitude,
                    longitude = entity.longitude,
                    timestamp = entity.timestamp.toString(), address = entity.address,
                )
            }
            locationAdapter.submitList(locationDataList)
        })
    }
    fun initControl() {
        binding.mDeleteDatabase.setOnClickListener {
            locationViewModel.deleteAllLocations()
        }
        binding.mSwitchCompat.setOnClickListener {
            SharedPreferenceUtil.getInstance(this).isToggleOn = binding.mSwitchCompat.isChecked
        }
        binding.btnTurnOnGps.setOnClickListener {
            if (!isLocationEnabled()) {
                Toast.makeText(this, "Please turn on GPS", Toast.LENGTH_SHORT).show()
                startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))

            } else {
                Toast.makeText(this, "GPS is already on", Toast.LENGTH_SHORT).show()
            }
        }
    }
    fun checkSwitch() {
        if (SharedPreferenceUtil.getInstance(this).isToggleOn) {
            binding.mSwitchCompat.isChecked = true
        }
        binding.mLastKnownLocation2.text = SharedPreferenceUtil.getInstance(this).mLastKnownLocation

    }
    fun setupAdapter() {
        locationAdapter = LocationAdapter()
        binding.recyclerView.adapter = locationAdapter
    }
    fun initViews() {
        locationViewModel = ViewModelProvider(this).get(LocationViewModel::class.java)
        requestPermissions()
        startForegroundService()
        checkSwitch()
        handler.post(runnable)
        scheduleLocationWork()
        setupAdapter()
    }
    fun startForegroundService() {
        val intent = Intent(this, ForegroundService::class.java)
        startService(intent)
    }
    fun isLocationEnabled(): Boolean {
        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
    fun requestPermissions() {
        if (!isLocationEnabled()) {
            openGPSSheet(this)
        }
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.PACKAGE_USAGE_STATS
                ),
                PERMISSION_REQUEST_CODE
            )
        }
    }
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions Granted!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permissions Denied!", Toast.LENGTH_SHORT).show()
            }
        }
        if (requestCode == REQUEST_LOCATION_PERMISSION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }

    }
}


