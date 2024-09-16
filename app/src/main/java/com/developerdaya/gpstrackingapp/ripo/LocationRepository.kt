package com.developerdaya.gpstrackingapp.ripo

import com.developerdaya.gpstrackingapp.room.LocationDao
import com.developerdaya.gpstrackingapp.room.LocationEntity

class LocationRepository(private val locationDao: LocationDao) {

    suspend fun insertLocation(locationEntity: LocationEntity) {
        locationDao.insert(locationEntity)
    }
}
