package com.example.aplikasikeuangan.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.aplikasikeuangan.database.AppDatabase
import com.example.aplikasikeuangan.model.Transaksi
import com.example.aplikasikeuangan.database.TransaksiRepository
import com.example.aplikasikeuangan.network.RetrofitClient
import kotlinx.coroutines.launch

class TransaksiViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TransaksiRepository
    val semuaTransaksi: androidx.lifecycle.LiveData<List<Transaksi>>

    init {
        val dao = AppDatabase.getDatabase(application).transaksiDao()
        repository = TransaksiRepository(dao)
        semuaTransaksi = repository.semuaTransaksi
    }

    fun insert(transaksi: Transaksi) = viewModelScope.launch {
        repository.insert(transaksi)
    }

    fun delete(transaksi: Transaksi) = viewModelScope.launch {
        repository.delete(transaksi)
    }

    fun update(transaksi: Transaksi) = viewModelScope.launch {
        repository.update(transaksi)
    }


}
