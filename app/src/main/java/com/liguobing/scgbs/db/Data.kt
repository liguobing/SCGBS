package com.liguobing.scgbs.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Data")
data class Data(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id", typeAffinity = ColumnInfo.INTEGER)
    val uid: Int = 0,
    @ColumnInfo(name = "Time")
    val time: String = "",
    @ColumnInfo(name = "Account")
    val account: String = "",
)