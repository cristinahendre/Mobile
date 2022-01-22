package com.example.template.database
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.template.dao.PersonDao
import com.example.template.domain.Person
import kotlinx.coroutines.CoroutineScope

@Database(entities = [Person::class], version = 1)
abstract class PersonDatabase: RoomDatabase() {
    abstract fun personDao(): PersonDao

    companion object {
        @Volatile
        private var INSTANCE: PersonDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): PersonDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    PersonDatabase::class.java,
                    "grade_db"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(GradeDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }

        private class GradeDatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {


        }
    }
}
