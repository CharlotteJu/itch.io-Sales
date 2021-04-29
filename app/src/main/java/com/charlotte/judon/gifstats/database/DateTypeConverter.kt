package com.charlotte.judon.gifstats.database

import androidx.room.TypeConverter
import java.util.*

/**
 * To convert [Date] in [Timestamp] in [RoomDatabase]
 * @author Charlotte JUDON
 */
class DateTypeConverter {

    @TypeConverter
    fun fromTimeStamp(value : Long?) : Date? {
        return if (value == null) null else Date((value))
    }

    @TypeConverter
    fun dateToTimestamp(date : Date?) : Long? {
        return date?.time
    }
}