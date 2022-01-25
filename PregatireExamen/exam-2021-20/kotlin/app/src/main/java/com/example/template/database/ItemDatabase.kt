package com.example.template.database
import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.template.dao.ItemDao
import com.example.template.domain.Item
import kotlinx.coroutines.CoroutineScope

@Database(entities = [Item::class], version = 1)
abstract class ItemDatabase: RoomDatabase() {
    abstract fun itemDao(): ItemDao

    companion object {
        @Volatile
        private var INSTANCE: ItemDatabase? = null

        fun getDatabase(
            context: Context,
            scope: CoroutineScope
        ): ItemDatabase {

            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ItemDatabase::class.java,
                    "item_db"
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
