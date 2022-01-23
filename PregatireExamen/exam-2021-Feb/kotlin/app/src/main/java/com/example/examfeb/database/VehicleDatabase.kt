package com.example.examfeb.database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.examfeb.dao.VehicleDao
import com.example.examfeb.domain.Vehicle
import kotlinx.coroutines.CoroutineScope


@Database(
    entities = [Vehicle::class], version = 2
)

abstract class VehicleDatabase : RoomDatabase() {
    abstract fun vehicleDao(): VehicleDao


    companion object {
        @Volatile
        private var INSTANCE: VehicleDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): VehicleDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    VehicleDatabase::class.java,
                    "vehicle_db"
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
                    .fallbackToDestructiveMigration()
                    .addCallback(VehicleDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        private class VehicleDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {

        }
    }
}
