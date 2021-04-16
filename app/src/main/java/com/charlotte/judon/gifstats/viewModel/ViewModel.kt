package com.charlotte.judon.gifstats.viewModel

import android.app.DownloadManager
import android.content.Context
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.charlotte.judon.gifstats.R
import com.charlotte.judon.gifstats.model.Sale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewModel(private val saleRepository: SaleRepository) : ViewModel() {

    fun getAllSales() = this.saleRepository.getAllSales()

    fun updateListSales(csvList : List<Sale>, roomList : List<Sale>, context: Context){
        val stringCsv = context.resources.getString(R.string.csv_integrate)
        viewModelScope.launch(Dispatchers.IO) {
            val thread = viewModelScope.async {
                for (sale in csvList) {
                    if (!roomList.contains(sale)) {
                        createSale(sale)
                    }
                }
            }
            thread.await()

            withContext(Dispatchers.Main) {
                Toast.makeText(context, stringCsv, Toast.LENGTH_LONG).show()
            }
        }
    }

    fun createSale(sale: Sale) {
        viewModelScope.launch {
            saleRepository.createSale(sale)
        }
    }

    fun updateSale(sale: Sale){
        viewModelScope.launch {
            saleRepository.updateSale(sale)
        }
    }

   fun deleteSale(sale: Sale, ) {
       viewModelScope.launch {
           saleRepository.deleteSale(sale)
       }
   }

    fun deleteAllSales(context: Context, list: List<Sale>) {
        viewModelScope.launch(Dispatchers.IO) {
            val thread = viewModelScope.async {
                for (sale in list) {
                    deleteSale(sale)
                }
            }
            thread.await()
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "TOUT EST SUPPRIME", Toast.LENGTH_LONG).show()
            }
        }
    }


}