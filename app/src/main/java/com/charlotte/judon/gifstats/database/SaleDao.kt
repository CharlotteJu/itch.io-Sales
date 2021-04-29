package com.charlotte.judon.gifstats.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.charlotte.judon.gifstats.model.Sale

/**
 * [Dao] Sale's interface for [RoomDatabase]
 * @author Charlotte JUDON
 */

@Dao
interface SaleDao {

    @Query("SELECT * from sale")
    fun getAllSales() : LiveData<List<Sale>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun createSale(sale : Sale) : Long

    @Update
    suspend fun updateSale(sale: Sale) : Int

    @Delete
    suspend fun deleteSale(sale: Sale)


}