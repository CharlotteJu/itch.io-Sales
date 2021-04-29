package com.charlotte.judon.gifstats.viewModel

import com.charlotte.judon.gifstats.database.SaleDao
import com.charlotte.judon.gifstats.model.Sale

/**
 * Class used by [ViewModel] based on [SaleDao]
 * @author Charlotte JUDON
 */
class SaleRepository (private val saleDao: SaleDao) {

    fun getAllSales() = this.saleDao.getAllSales()

    suspend fun createSale(sale: Sale) = this.saleDao.createSale(sale)

    suspend fun deleteSale(sale: Sale) = this.saleDao.deleteSale(sale)
}