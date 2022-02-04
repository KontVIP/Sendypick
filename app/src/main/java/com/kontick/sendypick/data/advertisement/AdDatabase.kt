package com.kontick.sendypick.data.advertisement

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [AdRoom::class], version = 1, exportSchema = false)
abstract class AdDatabase : RoomDatabase() {

    abstract fun adDao(): AdDao

    companion object {
        @Volatile
        private var INSTANCE: AdDatabase? = null

        fun getDatabase(context: Context): AdDatabase{
            val tempInstance = INSTANCE
            if(tempInstance != null){
                return tempInstance
            }
            synchronized(this){
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AdDatabase::class.java,
                    "ad_database"
                ).fallbackToDestructiveMigration().build()
                INSTANCE = instance
                return instance
            }
        }
    }

}