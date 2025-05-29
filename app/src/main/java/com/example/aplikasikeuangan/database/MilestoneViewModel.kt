package com.example.aplikasikeuangan.database

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.aplikasikeuangan.model.Milestone
import kotlinx.coroutines.launch

class MilestoneViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: MilestoneRepository

    val activeMilestone: LiveData<Milestone?>

    init {
        val dao = AppDatabase.getDatabase(application).milestoneDao()
        repository = MilestoneRepository(dao)
        activeMilestone = repository.activeMilestone
    }

    fun insert(milestone: Milestone) = viewModelScope.launch {
        repository.insert(milestone)
    }

    fun update(milestone: Milestone) = viewModelScope.launch {
        repository.update(milestone)
    }

    fun resetMilestone() = viewModelScope.launch {
        repository.clearAll()
    }

    fun sisihkanUang(jumlah: Int, onSuccess: (judul: String) -> Unit) {
        viewModelScope.launch {
            val milestone = activeMilestone.value
            if (milestone != null) {
                val terkumpulBaru = milestone.terkumpul + jumlah
                val updated = milestone.copy(terkumpul = terkumpulBaru)
                repository.update(updated)
                onSuccess(milestone.judul)
            }
        }
    }

}
