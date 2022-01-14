package com.example.roomversion.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.roomversion.dao.GradeDao
import com.example.roomversion.dao.StudentDao
import com.example.roomversion.dao.TeacherDao
import com.example.roomversion.domain.Converters
import com.example.roomversion.domain.Grade
import com.example.roomversion.domain.Student
import com.example.roomversion.domain.Teacher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate

@Database(entities = [Grade::class,
                        Student::class,
                            Teacher::class], version = 4)
@TypeConverters(Converters::class)
abstract class GradeDatabase: RoomDatabase() {
    abstract fun gradeDao(): GradeDao
    abstract fun studentDao(): StudentDao
    abstract fun teacherDao(): TeacherDao



    companion object {
        @Volatile
        private var INSTANCE: GradeDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): GradeDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    GradeDatabase::class.java,
                    "grade_db"
                )
                    // Wipes and rebuilds instead of migrating if no Migration object.
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
            /**
             * Override the onOpen method to populate the database.
             * For this sample, we clear the database every time it is created or opened.
             */
            override fun onOpen(db: SupportSQLiteDatabase) {
                super.onOpen(db)
                // If you want to keep the data through app restarts,
                // comment out the following line.
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.gradeDao())
                    }
                }
            }

            suspend fun populateDatabase(gradeDAO: GradeDao) {

//                gradeDAO.deleteAll()
//
//
//                var gr = Grade( 2, 1, 10, 2, LocalDate.parse("2010-09-10"),0);
//                gradeDAO.insert(gr);
//                gr = Grade( 1, 2, 1,2, LocalDate.parse("2019-01-12"),0);
//                gradeDAO.insert(gr);

            }
        }
    }
}
