package com.charlotte.judon.gifstats.viewModel

import android.content.Context
import androidx.lifecycle.ViewModelProvider
import com.charlotte.judon.gifstats.database.AppDatabase

class Injection {

    companion object {
        private fun getSaleDao(context: Context) = AppDatabase.getDatabase(context).saleDao()

        fun configViewModelFactory(context: Context) : ViewModelFactory {
            val saleRepository = SaleRepository(getSaleDao(context))

            return ViewModelFactory(saleRepository)
        }
    }
}