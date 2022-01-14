package com.example.roomversion.domain

import androidx.room.TypeConverter
import java.time.LocalDate

class Converters {
    @TypeConverter
    fun fromString(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(value);}
    }

    @TypeConverter
    fun toString(date: LocalDate?): String {
        return date.toString();
    }
}