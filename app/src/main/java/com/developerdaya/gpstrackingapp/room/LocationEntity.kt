package com.developerdaya.gpstrackingapp.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class LocationEntity(
    @PrimaryKey(autoGenerate = true) val id: Int,
    val latitude: Double,
    val longitude: Double,
    val timestamp: String,
    val address: String,
)
