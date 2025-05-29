package com.example.aplikasikeuangan.database

import androidx.lifecycle.LiveData
import com.example.aplikasikeuangan.model.Milestone

class MilestoneRepository(private val milestoneDao: MilestoneDao) {
    val activeMilestone: LiveData<Milestone?> = milestoneDao.getActiveMilestone()

    suspend fun insert(milestone: Milestone) {
        milestoneDao.deactivateAll()
        milestoneDao.insert(milestone.copy(aktif = true))
    }

    suspend fun update(milestone: Milestone) {
        milestoneDao.update(milestone)
    }

    suspend fun clearAll() {
        milestoneDao.clearAll()
    }
}
