package com.charlotte.judon.gifstats.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.charlotte.judon.gifstats.model.Sale

@Database (entities = [Sale::class], version = 1, exportSchema = false)
@TypeConverters(DateTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun  saleDao() : SaleDao

    companion object {
        @Volatile
        private var instance : AppDatabase? = null

        fun getDatabase(context: Context) : AppDatabase {
            val temp = this.instance

            if (temp!= null) {
                return temp
            }

            synchronized(AppDatabase::class) {
                this.instance = Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "DATABASE").build()
                return Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, "DATABASE").build()
            }
        }
    }


}