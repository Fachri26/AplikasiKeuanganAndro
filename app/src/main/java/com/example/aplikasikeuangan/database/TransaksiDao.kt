package com.example.aplikasikeuangan.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.aplikasikeuangan.model.Transaksi

@Dao
interface TransaksiDao {

    @Query("SELECT * FROM transaksi ORDER BY id DESC")
    fun getAll(): LiveData<List<Transaksi>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(transaksi: Transaksi)

    @Delete
    suspend fun delete(transaksi: Transaksi)

    @Update
    suspend fun update(transaksi: Transaksi)
}