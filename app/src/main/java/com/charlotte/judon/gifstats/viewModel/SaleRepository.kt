package com.charlotte.judon.gifstats.viewModel

import com.charlotte.judon.gifstats.database.SaleDao
import com.charlotte.judon.gifstats.model.Sale

class SaleRepository (private val saleDao: SaleDao) {

    fun getAllSales() = this.saleDao.getAllSales()

    suspend fun createSale(sale: Sale) = this.saleDao.createSale(sale)

    suspend fun updateSale(sale: Sale) = this.saleDao.updateSale(sale)

    suspend fun deleteSale(sale: Sale) = this.saleDao.deleteSale(sale)
}