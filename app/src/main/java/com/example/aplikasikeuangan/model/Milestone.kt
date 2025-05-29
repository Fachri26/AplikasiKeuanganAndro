package com.example.aplikasikeuangan.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "milestone")
data class Milestone(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val judul: String,
    val target: Int,
    val terkumpul: Int = 0,
    val aktif: Boolean = true 
)

