package com.example.aplikasikeuangan.ui.statistik

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import com.example.aplikasikeuangan.R
import com.example.aplikasikeuangan.database.MilestoneViewModel
import com.example.aplikasikeuangan.model.Milestone
import java.text.SimpleDateFormat
import java.util.*

class BuatMilestoneActivity : ComponentActivity() {

    private val milestoneViewModel: MilestoneViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        actionBar?.hide()
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buat_milestone)

        val edtJudul = findViewById<EditText>(R.id.edtJudulMilestone)
        val edtTarget = findViewById<EditText>(R.id.edtTargetMilestone)
        val btnSimpan = findViewById<Button>(R.id.btnSimpanMilestone)

        btnSimpan.setOnClickListener {
            val judul = edtJudul.text.toString().trim()
            val target = edtTarget.text.toString().toIntOrNull()

            if (judul.isEmpty() || target == null || target <= 0) {
                Toast.makeText(this, "Isi data dengan benar", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val milestone = Milestone(
                judul = judul,
                target = target,
                terkumpul = 0,
                aktif = true
            )

            milestoneViewModel.insert(milestone)
            Toast.makeText(this, "Milestone berhasil dibuat", Toast.LENGTH_SHORT).show()
            finish() // Kembali ke StatistikFragment
        }
    }
}