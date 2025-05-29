package com.example.aplikasikeuangan.database

import androidx.lifecycle.LiveData
import com.example.aplikasikeuangan.database.TransaksiDao
import com.example.aplikasikeuangan.model.Transaksi

class TransaksiRepository(private val transaksiDao: TransaksiDao) {
    val semuaTransaksi: LiveData<List<Transaksi>> = transaksiDao.getAll()

    suspend fun insert(transaksi: Transaksi) {
        transaksiDao.insert(transaksi)
    }

    suspend fun delete(transaksi: Transaksi) {
        transaksiDao.delete(transaksi)
    }

    suspend fun update(transaksi: Transaksi) {
        transaksiDao.update(transaksi)
    }
}
