package com.liguobing.scgbs.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow

@Dao
interface DataDao {
    @Query("SELECT * FROM Data order by id desc")
    fun getAllData(): Flow<List<Data>>


    @Insert
    fun insertData(data: Data):Long
}