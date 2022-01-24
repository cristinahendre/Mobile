package com.example.parkingapp.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "spaces")
data class Space(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(name = "number")
    var number: String,
    @ColumnInfo(name = "address")
    var address:String,
    @ColumnInfo(name = "status")
    var status:  String,
    @ColumnInfo(name = "count")
    var count:  Int,
    @ColumnInfo(name = "changed")
    var changed:Int
    //Changed = 0 (no changes), 1 (add), 2(delete), 3(update)


)