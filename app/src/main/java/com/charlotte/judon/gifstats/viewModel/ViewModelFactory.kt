package com.charlotte.judon.gifstats.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Class used to create [ViewModel]
 * @args provided by [Injection]
 * @author Charlotte JUDON
 */
class ViewModelFactory (private val saleRepository: SaleRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return modelClass.cast(ViewModel(saleRepository))!!
    }


}