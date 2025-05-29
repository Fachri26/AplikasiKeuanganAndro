package com.example.aplikasikeuangan.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.aplikasikeuangan.model.Milestone
import com.example.aplikasikeuangan.model.Transaksi

@Database(entities = [Transaksi::class, Milestone::class], version = 2, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun transaksiDao(): TransaksiDao
    abstract fun milestoneDao(): MilestoneDao // <-- Tambahkan ini

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "aplikasi_keuangan_db"
                )
                    .fallbackToDestructiveMigration() // <-- Tambahkan jika kamu tidak pakai Migration
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}

