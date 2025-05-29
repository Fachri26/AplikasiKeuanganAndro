package com.example.aplikasikeuangan.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "transaksi")
data class Transaksi(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val judul: String,
    val nominal: Int,
    val tanggal: String,
    val kategori: String
) : Parcelable
