package com.developerdaya.gpstrackingapp.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.developerdaya.gpstrackingapp.room.AppDatabase
import com.developerdaya.gpstrackingapp.room.LocationDao
import com.developerdaya.gpstrackingapp.room.LocationEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LocationViewModel(application: Application) : AndroidViewModel(application) {
    private val locationDao: LocationDao
    private val allLocations: LiveData<List<LocationEntity>>

    init {
        val db = Room.databaseBuilder(
            application,
            AppDatabase::class.java,
            "app_db"
        ).build()
        locationDao = db.locationDao()
        allLocations = locationDao.getAllLocations()
    }

    fun getAllLocations(): LiveData<List<LocationEntity>> {
        return allLocations
    }

    fun insertLocation(location: LocationEntity) {
        viewModelScope.launch(Dispatchers.IO) {
            locationDao.insert(location)
        }
    }

    fun deleteAllLocations() {
        viewModelScope.launch(Dispatchers.IO) {
            locationDao.deleteAll()
        }
    }


}
