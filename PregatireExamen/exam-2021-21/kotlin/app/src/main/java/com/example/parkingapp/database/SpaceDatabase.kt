package com.example.parkingapp.database


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.parkingapp.dao.SpaceDao
import com.example.parkingapp.domain.Space
import kotlinx.coroutines.CoroutineScope


@Database(
    entities = [Space::class], version = 1
)

abstract class SpaceDatabase : RoomDatabase() {
    abstract fun spaceDao(): SpaceDao


    companion object {
        @Volatile
        private var INSTANCE: SpaceDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): SpaceDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SpaceDatabase::class.java,
                    "space_db"
                ).fallbackToDestructiveMigration()
                    .addCallback(DbCallback(scope))
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DbCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback()
    }
}
