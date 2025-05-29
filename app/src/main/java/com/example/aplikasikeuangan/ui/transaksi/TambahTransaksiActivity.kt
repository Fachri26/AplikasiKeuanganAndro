package com.example.aplikasikeuangan.ui.transaksi

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.aplikasikeuangan.databinding.ActivityTambahTransaksiBinding
import com.example.aplikasikeuangan.model.Transaksi
import com.example.aplikasikeuangan.database.TransaksiViewModel
import java.util.*

class TambahTransaksiActivity : AppCompatActivity() {

    private lateinit var binding: ActivityTambahTransaksiBinding
    private val transaksiViewModel: TransaksiViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTambahTransaksiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val kategoriList = listOf("Pemasukan", "Pengeluaran")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, kategoriList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spKategori.adapter = adapter

        binding.etTanggal.setOnClickListener {
            showDatePickerDialog()
        }

        binding.btnSimpan.setOnClickListener {
            simpanTransaksi()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val dpd = DatePickerDialog(this,
            { _, year, month, day ->
                val tanggal = String.format("%04d-%02d-%02d", year, month + 1, day)
                binding.etTanggal.setText(tanggal)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dpd.show()
    }

    private fun simpanTransaksi() {
        val judul = binding.etJudul.text.toString().trim()
        val nominalText = binding.etNominal.text.toString().trim()
        val tanggal = binding.etTanggal.text.toString().trim()
        val kategori = binding.spKategori.selectedItem?.toString() ?: ""

        if (judul.isEmpty() || nominalText.isEmpty() || tanggal.isEmpty() || kategori.isEmpty()) {
            Toast.makeText(this, "Semua field harus diisi", Toast.LENGTH_SHORT).show()
            return
        }

        val nominal = nominalText.toIntOrNull()
        if (nominal == null) {
            Toast.makeText(this, "Nominal harus berupa angka", Toast.LENGTH_SHORT).show()
            return
        }

        val transaksi = Transaksi(
            judul = judul,
            nominal = nominal,
            tanggal = tanggal,
            kategori = kategori
        )

        transaksiViewModel.insert(transaksi)

        Toast.makeText(this, "Transaksi disimpan", Toast.LENGTH_SHORT).show()
        finish()
    }
}
