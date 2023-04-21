package com.liguobing.scgbs.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Data::class], version = 1)
abstract class DataBase : RoomDatabase() {
    abstract fun dataDao(): DataDao

    companion object {
        private var instance: DataBase? = null

        @Synchronized
        fun getInstance(context: Context): DataBase {
            if (instance == null) {
                instance = Room.databaseBuilder(
                    context,
                    DataBase::class.java, "Data"
                )
                    .build()
            }
            return instance!!
        }
    }
}