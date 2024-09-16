package com.developerdaya.gpstrackingapp.workManager

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.developerdaya.gpstrackingapp.room.AppDatabase
import com.developerdaya.gpstrackingapp.room.LocationEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date

class LocationWorker(context: Context, params: WorkerParameters) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            // Fetch the location (Assuming location is fetched here)
            val location: Location? = getLocation()

            if (location != null) {
                // Convert timestamp to readable date format
                val timestamp = System.currentTimeMillis()
                val formattedTimestamp = mTimeStampToDate(timestamp)

                // Create LocationEntity
                val locationEntity = LocationEntity(
                    id = 0,
                    latitude = location.latitude,
                    longitude = location.longitude,
                    timestamp = formattedTimestamp,
                    address = "Sample Address" // Replace this with actual address if available
                )

                // Insert location data into the database using DAO
                saveLocationToDatabase(locationEntity)

                Log.d("LocationWorker", "Saved Location: $locationEntity")
            } else {
                Log.e("LocationWorker", "Failed to retrieve location")
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("LocationWorker", "Error saving location", e)
            Result.failure()
        }
    }

    // Helper function to save location data to the database
    private suspend fun saveLocationToDatabase(locationEntity: LocationEntity) {
        val locationDao = AppDatabase.getDatabase(applicationContext).locationDao()
        withContext(Dispatchers.IO) {
            locationDao.insert(locationEntity)
        }
    }

    // Helper function to format the timestamp
    private fun mTimeStampToDate(value: Long): String {
        var timestamp = value
        if (timestamp.toString().length < 13) {
            timestamp *= 1000
        }
        val sdf = SimpleDateFormat("dd/MM/yyyy hh:mm:ss aa")
        return sdf.format(Date(timestamp))
    }

    // Placeholder for actual location fetching logic
    private fun getLocation(): Location? {
        return null
    }
}
