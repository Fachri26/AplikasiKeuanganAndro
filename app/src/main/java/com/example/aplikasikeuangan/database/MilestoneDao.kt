package com.example.aplikasikeuangan.database

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.aplikasikeuangan.model.Milestone

@Dao
interface MilestoneDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(milestone: Milestone)

    @Update
    suspend fun update(milestone: Milestone)

    @Query("SELECT * FROM milestone WHERE aktif = 1 LIMIT 1")
    fun getActiveMilestone(): LiveData<Milestone?>

    @Query("UPDATE milestone SET aktif = 0")
    suspend fun deactivateAll()

    @Query("DELETE FROM milestone")
    suspend fun clearAll()
}
