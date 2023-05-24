package com.hamdan.acceleromatorminiproject.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hamdan.acceleromatorminiproject.data.AccelerationDatabase.Companion.ACCELERATION_DB_VERSION

@Database(entities = [ScoreEntity::class], version = ACCELERATION_DB_VERSION)
abstract class AccelerationDatabase: RoomDatabase() {
    abstract fun accelerationDao(): AccelerationDao

    companion object {
        const val ACCELERATION_DB_VERSION = 1
        const val DATABASE_NAME = "acceleration_database"
    }
}