package com.example.nestflix.util

import androidx.room.TypeConverter
import java.util.*

class DateConverter {

    //convert from data
    @TypeConverter
    fun timeStampFromDate(date : Date): Long {
        return date.time
    }

    @TypeConverter
    fun dateFromTimestamp(timestamp : Long) : Date? {
        return Date(timestamp)
    }
}