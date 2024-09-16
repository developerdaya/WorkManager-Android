package com.developerdaya.gpstrackingapp.room

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface LocationDao {

    @Insert
    suspend fun insert(location: LocationEntity)

    @Query("SELECT * FROM LocationEntity ORDER BY timestamp DESC")
    fun getAllLocations(): LiveData<List<LocationEntity>>

    @Query("DELETE FROM LocationEntity")
    suspend fun deleteAll()

}
