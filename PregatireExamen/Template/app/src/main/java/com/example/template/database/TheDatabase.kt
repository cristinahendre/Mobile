package com.example.template.database
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.template.dao.MyDao
import com.example.template.domain.Person
import kotlinx.coroutines.CoroutineScope

@Database(entities = [Person::class], version = 1)
abstract class TheDatabase: RoomDatabase() {
    abstract fun myDao(): MyDao

    companion object {
        @Volatile
        private var INSTANCE: TheDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): TheDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TheDatabase::class.java,
                    "grade_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(MyDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        private class MyDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {


        }
    }
}
